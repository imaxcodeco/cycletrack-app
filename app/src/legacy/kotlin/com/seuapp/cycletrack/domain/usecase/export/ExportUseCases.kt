package com.seuapp.cycletrack.domain.usecase.export

import com.seuapp.cycletrack.domain.model.Cycle
import com.seuapp.cycletrack.domain.model.Symptom
import com.seuapp.cycletrack.domain.model.Prediction
import com.seuapp.cycletrack.domain.model.Pregnancy
import com.seuapp.cycletrack.domain.model.PartnerConnection
import com.seuapp.cycletrack.domain.model.HealthDataPoint
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.model.Subscription
import com.seuapp.cycletrack.domain.model.ExportRequest
import com.seuapp.cycletrack.domain.model.ExportFormat
import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import com.seuapp.cycletrack.domain.repository.PredictionRepository
import com.seuapp.cycletrack.domain.repository.PregnancyRepository
import com.seuapp.cycletrack.domain.repository.PartnerRepository
import com.seuapp.cycletrack.domain.repository.HealthDataPointRepository
import com.seuapp.cycletrack.domain.repository.UserProfileRepository
import com.seuapp.cycletrack.domain.repository.SubscriptionRepository
import com.seuapp.cycletrack.domain.repository.ExportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.zip.ZipOutputStream
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Casos de uso para Exportação de Dados (LGPD/GDPR Art. 20).
 * Clean-room - gera exportação completa em JSON/CSV/PDF.
 */
class ExportUseCases(
    private val exportRepository: ExportRepository,
    private val cycleRepository: CycleRepository,
    private val symptomRepository: SymptomRepository,
    private val predictionRepository: PredictionRepository,
    private val pregnancyRepository: PregnancyRepository,
    private val partnerRepository: PartnerRepository,
    private val healthDataRepository: HealthDataPointRepository,
    private val userProfileRepository: UserProfileRepository,
    private val subscriptionRepository: SubscriptionRepository
) {

    /**
     * Solicita exportação de dados.
     */
    suspend fun requestExport(
        userId: String,
        format: ExportFormat = ExportFormat.JSON,
        includeCycles: Boolean = true,
        includeSymptoms: Boolean = true,
        includePredictions: Boolean = true,
        includePregnancy: Boolean = true,
        includePartnerData: Boolean = false,
        includeHealthConnectData: Boolean = false,
        includeSettings: Boolean = true,
        includeContent: Boolean = false,
        startDate: kotlinx.datetime.LocalDate? = null,
        endDate: kotlinx.datetime.LocalDate? = null,
        anonymize: Boolean = false,
        requestedFrom: String = "app",
        ipAddress: String = "",
        userAgent: String = ""
    ): ExportRequest {
        return exportRepository.createExport(
            userId = userId,
            format = format.key,
            includeCycles = includeCycles,
            includeSymptoms = includeSymptoms,
            includePredictions = includePredictions,
            includePregnancy = includePregnancy,
            includePartnerData = includePartnerData,
            includeHealthConnectData = includeHealthConnectData,
            includeSettings = includeSettings,
            includeContent = includeContent,
            dateRangeStart = startDate?.toEpochMilliseconds() ?: 0L,
            dateRangeEnd = endDate?.toEpochMilliseconds() ?: Long.MAX_VALUE,
            anonymize = anonymize,
            requestedFrom = requestedFrom,
            ipAddress = ipAddress,
            userAgent = userAgent
        )
    }

    /**
     * Processa exportação em background.
     */
    suspend fun processExport(exportId: String) {
        val export = exportRepository.getExport(exportId)
            ?: throw IllegalArgumentException("Exportação não encontrada")

        exportRepository.markAsProcessing(exportId)

        try {
            val userId = export.userId
            val startDate = export.dateRangeStart
            val endDate = export.dateRangeEnd
            val anonymize = export.anonymize

            // Coleta todos os dados
            val cycles = if (export.includeCycles) {
                cycleRepository.getCyclesForExport(userId, startDate, endDate, anonymize)
            } else emptyList()

            val symptoms = if (export.includeSymptoms) {
                symptomRepository.getSymptomsForExport(userId, startDate, endDate, anonymize)
            } else emptyList()

            val predictions = if (export.includePredictions) {
                predictionRepository.getPredictionsForExport(userId, startDate, endDate, anonymize)
            } else emptyList()

            val pregnancies = if (export.includePregnancy) {
                val allPregnancies = pregnancyRepository.getPregnancies(userId)
                allPregnancies.filter { p ->
                    p.createdAt >= startDate && p.createdAt <= endDate
                }
            } else emptyList()

            val partnerData = if (export.includePartnerData) {
                partnerRepository.getPartners(userId)
            } else emptyList()

            val healthData = if (export.includeHealthConnectData) {
                healthDataRepository.getHealthDataForExport(userId, startDate, endDate, anonymize)
            } else emptyList()

            val profile = if (export.includeSettings) {
                userProfileRepository.getProfileForExport(userId, anonymize)
            } else null

            val subscription = subscriptionRepository.getActiveSubscription(userId)

            // Gera arquivo baseado no formato
            val (filePath, fileSize, fileHash) = when (export.format) {
                ExportRequest.FORMAT_JSON -> generateJsonExport(
                    exportId, cycles, symptoms, predictions, pregnancies,
                    partnerData, healthData, profile, subscription, anonymize
                )
                ExportRequest.FORMAT_CSV -> generateCsvExport(
                    exportId, cycles, symptoms, predictions, pregnancies,
                    partnerData, healthData, profile, subscription, anonymize
                )
                ExportRequest.FORMAT_PDF -> generatePdfExport(
                    exportId, cycles, symptoms, predictions, pregnancies,
                    partnerData, healthData, profile, subscription, anonymize
                )
                else -> throw IllegalArgumentException("Formato não suportado: ${export.format}")
            }

            // Marca como concluída
            val downloadUrl = "" // TODO: Upload para storage assinado
            exportRepository.markAsCompleted(
                exportId = exportId,
                filePath = filePath,
                fileSize = fileSize,
                fileHash = fileHash,
                downloadUrl = downloadUrl
            )

        } catch (e: Exception) {
            exportRepository.markAsFailed(exportId, e.message ?: "Erro desconhecido")
            throw e
        }
    }

    /**
     * Gera exportação JSON.
     */
    private fun generateJsonExport(
        exportId: String,
        cycles: List<Cycle>,
        symptoms: List<Symptom>,
        predictions: List<Prediction>,
        pregnancies: List<Pregnancy>,
        partnerData: List<PartnerConnection>,
        healthData: List<HealthDataPoint>,
        profile: UserProfile?,
        subscription: Subscription?,
        anonymize: Boolean
    ): Triple<String, Long, String> {
        val exportData = mapOf(
            "exportId" to exportId,
            "exportedAt" to System.currentTimeMillis(),
            "version" to "1.0",
            "anonymized" to anonymize,
            "cycles" to cycles.map { it.toJsonMap(anonymize) },
            "symptoms" to symptoms.map { it.toJsonMap(anonymize) },
            "predictions" to predictions.map { it.toJsonMap(anonymize) },
            "pregnancies" to pregnancies.map { it.toJsonMap(anonymize) },
            "partnerConnections" to partnerData.map { it.toJsonMap(anonymize) },
            "healthDataPoints" to healthData.map { it.toJsonMap(anonymize) },
            "profile" to profile?.toJsonMap(anonymize),
            "subscription" to subscription?.toJsonMap(anonymize)
        )

        val json = kotlinx.serialization.json.Json.encodeToString(
            kotlinx.serialization.json.JsonConfiguration.Default,
            kotlinx.serialization.json.JsonElementSerializer,
            exportData
        )

        return writeExportFile(exportId, "json", json.toByteArray())
    }

    /**
     * Gera exportação CSV (um arquivo ZIP com múltiplos CSVs).
     */
    private fun generateCsvExport(
        exportId: String,
        cycles: List<Cycle>,
        symptoms: List<Symptom>,
        predictions: List<Prediction>,
        pregnancies: List<Pregnancy>,
        partnerData: List<PartnerConnection>,
        healthData: List<HealthDataPoint>,
        profile: UserProfile?,
        subscription: Subscription?,
        anonymize: Boolean
    ): Triple<String, Long, String> {
        val tempDir = File(java.io.File.createTempFile("export_", "").parentFile, "export_$exportId")
        tempDir.mkdirs()

        try {
            // Escreve CSVs individuais
            writeCsv(File(tempDir, "cycles.csv"), cycles.map { it.toCsvRow(anonymize) }, Cycle.csvHeaders())
            writeCsv(File(tempDir, "symptoms.csv"), symptoms.map { it.toCsvRow(anonymize) }, Symptom.csvHeaders())
            writeCsv(File(tempDir, "predictions.csv"), predictions.map { it.toCsvRow(anonymize) }, Prediction.csvHeaders())
            writeCsv(File(tempDir, "pregnancies.csv"), pregnancies.map { it.toCsvRow(anonymize) }, Pregnancy.csvHeaders())
            writeCsv(File(tempDir, "partners.csv"), partnerData.map { it.toCsvRow(anonymize) }, PartnerConnection.csvHeaders())
            writeCsv(File(tempDir, "health_data.csv"), healthData.map { it.toCsvRow(anonymize) }, HealthDataPoint.csvHeaders())
            
            if (profile != null) {
                writeCsv(File(tempDir, "profile.csv"), listOf(profile.toCsvRow(anonymize)), UserProfile.csvHeaders())
            }
            if (subscription != null) {
                writeCsv(File(tempDir, "subscription.csv"), listOf(subscription.toCsvRow(anonymize)), Subscription.csvHeaders())
            }

            // Cria ZIP
            val zipFile = File(tempDir.parentFile, "export_$exportId.zip")
            zipDirectory(tempDir, zipFile)

            return writeExportFile(exportId, "zip", zipFile.readBytes())
        } finally {
            // Limpa diretório temporário
            tempDir.deleteRecursively()
        }
    }

    /**
     * Gera exportação PDF (relatório formatado).
     */
    private fun generatePdfExport(
        exportId: String,
        cycles: List<Cycle>,
        symptoms: List<Symptom>,
        predictions: List<Prediction>,
        pregnancies: List<Pregnancy>,
        partnerData: List<PartnerConnection>,
        healthData: List<HealthDataPoint>,
        profile: UserProfile?,
        subscription: Subscription?,
        anonymize: Boolean
    ): Triple<String, Long, String> {
        // TODO: Implementar com iText 7
        // Por enquanto, gera JSON e renomeia
        val (path, size, hash) = generateJsonExport(
            exportId, cycles, symptoms, predictions, pregnancies,
            partnerData, healthData, profile, subscription, anonymize
        )
        val pdfFile = File(path).copyTo(File(path.replace(".json", ".pdf")))
        return Triple(pdfFile.absolutePath, pdfFile.length(), hash)
    }

    /**
     * Escreve arquivo de exportação e calcula hash.
     */
    private fun writeExportFile(exportId: String, extension: String, data: ByteArray): Triple<String, Long, String> {
        val file = File(java.io.File.createTempFile("export_$exportId.", ".$extension"))
        FileOutputStream(file).use { it.write(data) }
        
        val hash = calculateSha256(data)
        return Triple(file.absolutePath, data.size.toLong(), hash)
    }

    /**
     * Calcula SHA-256.
     */
    private fun calculateSha256(data: ByteArray): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data)
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Escreve CSV.
     */
    private fun writeCsv(file: File, rows: List<List<String>>, headers: List<String>) {
        file.bufferedWriter().use { writer ->
            writer.writeLine(headers.joinToString(","))
            for (row in rows) {
                writer.writeLine(row.joinToString(","))
            }
        }
    }

    /**
     * Compacta diretório em ZIP.
     */
    private fun zipDirectory(sourceDir: File, zipFile: File) {
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            sourceDir.walk().filter { it.isFile }.forEach { file ->
                val relativePath = sourceDir.toPath().relativize(file.toPath()).toString()
                zipOut.putNextEntry(java.util.zip.ZipEntry(relativePath))
                file.copyTo(zipOut)
                zipOut.closeEntry()
            }
        }
    }

    /**
     * Obtém exportações do usuário.
     */
    suspend fun getUserExports(userId: String): List<ExportRequest> {
        return exportRepository.getExports(userId)
    }

    /**
     * Obtém exportação por ID.
     */
    suspend fun getExport(exportId: String): ExportRequest? {
        return exportRepository.getExport(exportId)
    }

    /**
     * Deleta exportação.
     */
    suspend fun deleteExport(exportId: String): Boolean {
        return exportRepository.deleteExport(exportId)
    }

    /**
     * Obtém estatísticas de exportação.
     */
    suspend fun getExportStats(userId: String) {
        return exportRepository.getExportStats(userId)
    }
}