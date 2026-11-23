package com.seuapp.cycletrack.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormatter
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import com.seuapp.cycletrack.domain.model.HealthDataPoint
import import kotlinx.datetime.TimeZone

/**
 * Extensões para serialização e exportação dos modelos de domínio.
 */
private val ISO_DATE = DateTimeFormatter.ISO_DATE
private val ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME

fun Cycle.toJsonMap(anonymize: Boolean): JsonObject {
    val builder = mutableMapOf<String, JsonElement>()
    if (!anonymize) {
        builder.put("id", JsonPrimitive(id))
        builder.put("userId", JsonPrimitive(userId))
    }
    builder.put("startDate", JsonPrimitive(startDate.toString()))
    endDate?.let { builder.put("endDate", JsonPrimitive(it.toString())) }
    builder.put("cycleLength", JsonPrimitive(cycleLength))
    builder.put("periodLength", JsonPrimitive(periodLength))
    builder.put("lutealPhaseLength", JsonPrimitive(lutealPhaseLength))
    ovulationDate?.let { builder.put("ovulationDate", JsonPrimitive(it.toString())) }
    fertileWindowStart?.let { builder.put("fertileWindowStart", JsonPrimitive(it.toString())) }
    fertileWindowEnd?.let { builder.put("fertileWindowEnd", JsonPrimitive(it.toString())) }
    builder.put("isPredicted", JsonPrimitive(isPredicted))
    builder.put("confidenceScore", JsonPrimitive(confidenceScore))
    builder.put("source", JsonPrimitive(source))
    builder.put("notes", JsonPrimitive(notes))
    builder.put("createdAt", JsonPrimitive(createdAt))
    builder.put("updatedAt", JsonPrimitive(updatedAt))
    return JsonObject(builder)
}

fun Cycle.toCsvRow(anonymize: Boolean): List<String> {
    return listOf(
        if (anonymize) "" else id,
        if (anonymize) "" else userId,
        startDate.toString(),
        endDate?.toString() ?: "",
        cycleLength.toString(),
        periodLength.toString(),
        lutealPhaseLength.toString(),
        ovulationDate?.toString() ?: "",
        fertileWindowStart?.toString() ?: "",
        fertileWindowEnd?.toString() ?: "",
        isPredicted.toString(),
        confidenceScore.toString(),
        source,
        notes.replace(",", ";").replace("\n", " "),
        createdAt.toString(),
        updatedAt.toString()
    )
}

fun Cycle.companion.csvHeaders(): List<String> {
    return listOf(
        "id", "userId", "startDate", "endDate", "cycleLength", "periodLength",
        "lutealPhaseLength", "ovulationDate", "fertileWindowStart", "fertileWindowEnd",
        "isPredicted", "confidenceScore", "source", "notes", "createdAt", "updatedAt"
    )
}

fun Symptom.toJsonMap(anonymize: Boolean): JsonObject {
    val builder = mutableMapOf<String, JsonElement>()
    if (!anonymize) {
        builder.put("id", JsonPrimitive(id))
        builder.put("userId", JsonPrimitive(userId))
        builder.put("cycleId", JsonPrimitive(cycleId))
    }
    builder.put("date", JsonPrimitive(date.toString()))
    builder.put("category", JsonPrimitive(category))
    builder.put("name", JsonPrimitive(name))
    builder.put("severity", JsonPrimitive(severity.value))
    builder.put("notes", JsonPrimitive(notes))
    builder.put("source", JsonPrimitive(source))
    builder.put("createdAt", JsonPrimitive(createdAt))
    builder.put("updatedAt", JsonPrimitive(updatedAt))
    return JsonObject(builder)
}

fun Symptom.toCsvRow(anonymize: Boolean): List<String> {
    return listOf(
        if (anonymize) "" else id,
        if (anonymize) "" else userId,
        if (anonymize) "" else cycleId,
        date.toString(),
        category,
        name,
        severity.value.toString(),
        notes.replace(",", ";").replace("\n", " "),
        source,
        createdAt.toString(),
        updatedAt.toString()
    )
}

fun Symptom.companion.csvHeaders(): List<String> {
    return listOf(
        "id", "userId", "cycleId", "date", "category", "name",
        "severity", "notes", "source", "createdAt", "updatedAt"
    )
}

fun Prediction.toJsonMap(anonymize: Boolean): JsonObject {
    val builder = mutableMapOf<String, JsonElement>()
    if (!anonymize) {
        builder.put("id", JsonPrimitive(id))
        builder.put("userId", JsonPrimitive(userId))
    }
    builder.put("type", JsonPrimitive(type.key))
    builder.put("predictedDate", JsonPrimitive(predictedDate.toString()))
    builder.put("confidenceScore", JsonPrimitive(confidenceScore))
    builder.put("algorithmVersion", JsonPrimitive(algorithmVersion))
    builder.put("cycleCountUsed", JsonPrimitive(cycleCountUsed))
    builder.put("standardDeviation", JsonPrimitive(standardDeviation))
    builder.put("metadata", JsonPrimitive(metadata))
    builder.put("source", JsonPrimitive(source.key))
    builder.put("isNotified", JsonPrimitive(isNotified))
    builder.put("notifiedAt", JsonPrimitive(notifiedAt))
    builder.put("createdAt", JsonPrimitive(createdAt))
    builder.put("updatedAt", JsonPrimitive(updatedAt))
    builder.put("expiresAt", JsonPrimitive(expiresAt))
    actualDate?.let { builder.put("actualDate", JsonPrimitive(it.toString())) }
    builder.put("accuracyDays", JsonPrimitive(accuracyDays))
    return JsonObject(builder)
}

fun Prediction.toCsvRow(anonymize: Boolean): List<String> {
    return listOf(
        if (anonymize) "" else id,
        if (anonymize) "" else userId,
        type.key,
        predictedDate.toString(),
        confidenceScore.toString(),
        algorithmVersion,
        cycleCountUsed.toString(),
        standardDeviation.toString(),
        metadata.replace(",", ";").replace("\n", " "),
        source.key,
        isNotified.toString(),
        notifiedAt.toString(),
        createdAt.toString(),
        updatedAt.toString(),
        expiresAt.toString(),
        actualDate?.toString() ?: "",
        accuracyDays.toString()
    )
}

fun Prediction.companion.csvHeaders(): List<String> {
    return listOf(
        "id", "userId", "type", "predictedDate", "confidenceScore", "algorithmVersion",
        "cycleCountUsed", "standardDeviation", "metadata", "source", "isNotified",
        "notifiedAt", "createdAt", "updatedAt", "expiresAt", "actualDate", "accuracyDays"
    )
}

fun Pregnancy.toJsonMap(anonymize: Boolean): JsonObject {
    val builder = mutableMapOf<String, JsonElement>()
    if (!anonymize) {
        builder.put("id", JsonPrimitive(id))
        builder.put("userId", JsonPrimitive(userId))
    }
    builder.put("lmpDate", JsonPrimitive(lmpDate.toString()))
    conceptionDate?.let { builder.put("conceptionDate", JsonPrimitive(it.toString())) }
    builder.put("dueDate", JsonPrimitive(dueDate.toString()))
    actualBirthDate?.let { builder.put("actualBirthDate", JsonPrimitive(it.toString())) }
    builder.put("currentWeek", JsonPrimitive(currentWeek))
    builder.put("currentDay", JsonPrimitive(currentDay))
    builder.put("trimester", JsonPrimitive(trimester))
    builder.put("status", JsonPrimitive(status.key))
    outcome?.let { builder.put("outcome", JsonPrimitive(it.key)) }
    outcomeDate?.let { builder.put("outcomeDate", JsonPrimitive(it.toString())) }
    builder.put("outcomeNotes", JsonPrimitive(outcomeNotes))
    builder.put("prePregnancyWeightKg", JsonPrimitive(prePregnancyWeightKg))
    builder.put("currentWeightKg", JsonPrimitive(currentWeightKg))
    builder.put("totalWeightGainKg", JsonPrimitive(totalWeightGainKg))
    builder.put("bloodPressureSystolic", JsonPrimitive(bloodPressureSystolic))
    builder.put("bloodPressureDiastolic", JsonPrimitive(bloodPressureDiastolic))
    builder.put("glucoseLevel", JsonPrimitive(glucoseLevel))
    builder.put("babyName", JsonPrimitive(babyName))
    builder.put("babyGender", JsonPrimitive(babyGender.key))
    builder.put("babyWeightGrams", JsonPrimitive(babyWeightGrams))
    builder.put("babyHeightCm", JsonPrimitive(babyHeightCm))
    builder.put("babyApgarScore", JsonPrimitive(babyApgarScore))
    builder.put("createdAt", JsonPrimitive(createdAt))
    builder.put("updatedAt", JsonPrimitive(updatedAt))
    return JsonObject(builder)
}

fun Pregnancy.toCsvRow(anonymize: Boolean): List<String> {
    return listOf(
        if (anonymize) "" else id,
        if (anonymize) "" else userId,
        lmpDate.toString(),
        conceptionDate?.toString() ?: "",
        dueDate.toString(),
        actualBirthDate?.toString() ?: "",
        currentWeek.toString(),
        currentDay.toString(),
        trimester.toString(),
        status.key,
        outcome?.key ?: "",
        outcomeDate?.toString() ?: "",
        outcomeNotes.replace(",", ";").replace("\n", " "),
        prePregnancyWeightKg.toString(),
        currentWeightKg.toString(),
        totalWeightGainKg.toString(),
        bloodPressureSystolic.toString(),
        bloodPressureDiastolic.toString(),
        glucoseLevel.toString(),
        babyName,
        babyGender.key,
        babyWeightGrams.toString(),
        babyHeightCm.toString(),
        babyApgarScore.toString(),
        createdAt.toString(),
        updatedAt.toString()
    )
}

fun Pregnancy.companion.csvHeaders(): List<String> {
    return listOf(
        "id", "userId", "lmpDate", "conceptionDate", "dueDate", "actualBirthDate",
        "currentWeek", "currentDay", "trimester", "status", "outcome", "outcomeDate",
        "outcomeNotes", "prePregnancyWeightKg", "currentWeightKg", "totalWeightGainKg",
        "bloodPressureSystolic", "bloodPressureDiastolic", "glucoseLevel",
        "babyName", "babyGender", "babyWeightGrams", "babyHeightCm", "babyApgarScore",
        "createdAt", "updatedAt"
    )
}

fun PartnerConnection.toJsonMap(anonymize: Boolean): JsonObject {
    val builder = mutableMapOf<String, JsonElement>()
    if (!anonymize) {
        builder.put("id", JsonPrimitive(id))
        builder.put("userId", JsonPrimitive(userId))
        builder.put("partnerUserId", JsonPrimitive(partnerUserId))
        builder.put("invitationCode", JsonPrimitive(invitationCode))
        builder.put("partnerEmail", JsonPrimitive(partnerEmail))
    }
    builder.put("partnerDisplayName", JsonPrimitive(partnerDisplayName))
    builder.put("partnerAvatarUrl", JsonPrimitive(partnerAvatarUrl))
    builder.put("status", JsonPrimitive(status.key))
    builder.put("invitedAt", JsonPrimitive(invitedAt))
    acceptedAt?.let { if (it > 0) builder.put("acceptedAt", JsonPrimitive(it)) }
    revokedAt?.let { if (it > 0) builder.put("revokedAt", JsonPrimitive(it)) }
    
    // Preferências de compartilhamento
    builder.put("shareCycle", JsonPrimitive(shareCycle))
    builder.put("sharePeriodDates", JsonPrimitive(sharePeriodDates))
    builder.put("shareOvulation", JsonPrimitive(shareOvulation))
    builder.put("shareFertileWindow", JsonPrimitive(shareFertileWindow))
    builder.put("sharePms", JsonPrimitive(sharePms))
    builder.put("shareSymptoms", JsonPrimitive(shareSymptoms))
    builder.put("sharePredictions", JsonPrimitive(sharePredictions))
    builder.put("sharePregnancy", JsonPrimitive(sharePregnancy))
    builder.put("shareInsights", JsonPrimitive(shareInsights))
    builder.put("shareMood", JsonPrimitive(shareMood))
    builder.put("shareWeight", JsonPrimitive(shareWeight))
    builder.put("shareNotes", JsonPrimitive(shareNotes))
    
    // Notificações para parceiro
    builder.put("notifyPartnerPeriodStart", JsonPrimitive(notifyPartnerPeriodStart))
    builder.put("notifyPartnerPeriodEnd", JsonPrimitive(notifyPartnerPeriodEnd))
    builder.put("notifyPartnerFertileWindow", JsonPrimitive(notifyPartnerFertileWindow))
    builder.put("notifyPartnerOvulation", JsonPrimitive(notifyPartnerOvulation))
    builder.put("notifyPartnerPms", JsonPrimitive(notifyPartnerPms))
    builder.put("notifyPartnerPregnancy", JsonPrimitive(notifyPartnerPregnancy))
    builder.put("notifyPartnerSymptoms", JsonPrimitive(notifyPartnerSymptoms))
    
    // Compartilhamento do parceiro
    builder.put("partnerShareCycle", JsonPrimitive(partnerShareCycle))
    builder.put("partnerSharePeriodDates", JsonPrimitive(partnerSharePeriodDates))
    builder.put("partnerShareOvulation", JsonPrimitive(partnerShareOvulation))
    builder.put("partnerShareFertileWindow", JsonPrimitive(partnerShareFertileWindow))
    builder.put("partnerSharePms", JsonPrimitive(partnerSharePms))
    builder.put("partnerShareSymptoms", JsonPrimitive(partnerShareSymptoms))
    builder.put("partnerSharePredictions", JsonPrimitive(partnerSharePredictions))
    builder.put("partnerSharePregnancy", JsonPrimitive(partnerSharePregnancy))
    builder.put("partnerShareInsights", JsonPrimitive(partnerShareInsights))
    builder.put("partnerShareMood", JsonPrimitive(partnerShareMood))
    builder.put("partnerShareWeight", JsonPrimitive(partnerShareWeight))
    builder.put("partnerShareNotes", JsonPrimitive(partnerShareNotes))
    
    builder.put("createdAt", JsonPrimitive(createdAt))
    builder.put("updatedAt", JsonPrimitive(updatedAt))
    return JsonObject(builder)
}

fun PartnerConnection.toCsvRow(anonymize: Boolean): List<String> {
    return listOf(
        if (anonymize) "" else id,
        if (anonymize) "" else userId,
        if (anonymize) "" else partnerUserId,
        if (anonymize) "" else invitationCode,
        if (anonymize) "" else partnerEmail,
        partnerDisplayName,
        partnerAvatarUrl,
        status.key,
        invitedAt.toString(),
        acceptedAt?.toString() ?: "",
        revokedAt?.toString() ?: "",
        shareCycle.toString(),
        sharePeriodDates.toString(),
        shareOvulation.toString(),
        shareFertileWindow.toString(),
        sharePms.toString(),
        shareSymptoms.toString(),
        sharePredictions.toString(),
        sharePregnancy.toString(),
        shareInsights.toString(),
        shareMood.toString(),
        shareWeight.toString(),
        shareNotes.toString(),
        notifyPartnerPeriodStart.toString(),
        notifyPartnerPeriodEnd.toString(),
        notifyPartnerFertileWindow.toString(),
        notifyPartnerOvulation.toString(),
        notifyPartnerPms.toString(),
        notifyPartnerPregnancy.toString(),
        notifyPartnerSymptoms.toString(),
        partnerShareCycle.toString(),
        partnerSharePeriodDates.toString(),
        partnerShareOvulation.toString(),
        partnerShareFertileWindow.toString(),
        partnerSharePms.toString(),
        partnerShareSymptoms.toString(),
        partnerSharePredictions.toString(),
        partnerSharePregnancy.toString(),
        partnerShareInsights.toString(),
        partnerShareMood.toString(),
        partnerShareWeight.toString(),
        partnerShareNotes.toString(),
        createdAt.toString(),
        updatedAt.toString()
    )
}

fun PartnerConnection.companion.csvHeaders(): List<String> {
    return listOf(
        "id", "userId", "partnerUserId", "invitationCode", "partnerEmail",
        "partnerDisplayName", "partnerAvatarUrl", "status", "invitedAt", "acceptedAt", "revokedAt",
        "shareCycle", "sharePeriodDates", "shareOvulation", "shareFertileWindow", "sharePms",
        "shareSymptoms", "sharePredictions", "sharePregnancy", "shareInsights", "shareMood",
        "shareWeight", "shareNotes",
        "notifyPartnerPeriodStart", "notifyPartnerPeriodEnd", "notifyPartnerFertileWindow",
        "notifyPartnerOvulation", "notifyPartnerPms", "notifyPartnerPregnancy", "notifyPartnerSymptoms",
        "partnerShareCycle", "partnerSharePeriodDates", "partnerShareOvulation",
        "partnerShareFertileWindow", "partnerSharePms", "partnerShareSymptoms",
        "partnerSharePredictions", "partnerSharePregnancy", "partnerShareInsights",
        "partnerShareMood", "partnerShareWeight", "partnerShareNotes",
        "createdAt", "updatedAt"
    )
}

fun HealthDataPoint.toJsonMap(anonymize: Boolean): JsonObject {
    val builder = mutableMapOf<String, JsonElement>()
    if (!anonymize) {
        builder.put("id", JsonPrimitive(id))
        builder.put("userId", JsonPrimitive(userId))
    }
    builder.put("dataType", JsonPrimitive(dataType))
    builder.put("startTime", JsonPrimitive(startTime))
    builder.put("endTime", JsonPrimitive(endTime))
    builder.put("timeZoneOffset", JsonPrimitive(timeZoneOffset))
    builder.put("value", JsonPrimitive(value))
    builder.put("numericValue", JsonPrimitive(numericValue))
    builder.put("unit", JsonPrimitive(unit))
    builder.put("source", JsonPrimitive(source))
    builder.put("sourcePackageName", JsonPrimitive(sourcePackageName))
    builder.put("sourceRecordId", JsonPrimitive(sourceRecordId))
    builder.put("flowLevel", JsonPrimitive(flowLevel))
    builder.put("testResult", JsonPrimitive(testResult))
    builder.put("temperatureCelsius", JsonPrimitive(temperatureCelsius))
    builder.put("mucusConsistency", JsonPrimitive(mucusConsistency))
    builder.put("pregnancyTestResult", JsonPrimitive(pregnancyTestResult))
    builder.put("syncStatus", JsonPrimitive(syncStatus))
    builder.put("lastSyncAttempt", JsonPrimitive(lastSyncAttempt))
    builder.put("syncError", JsonPrimitive(syncError))
    builder.put("conflictResolution", JsonPrimitive(conflictResolution))
    builder.put("createdAt", JsonPrimitive(createdAt))
    builder.put("updatedAt", JsonPrimitive(updatedAt))
    return JsonObject(builder)
}

fun HealthDataPoint.toCsvRow(anonymize: Boolean): List<String> {
    return listOf(
        if (anonymize) "" else id,
        if (anonymize) "" else userId,
        dataType,
        LocalDateTime.ofEpochMilliseconds(startTime, kotlinx.datetime.TimeZone.currentSystemDefault()).toString(),
        LocalDateTime.ofEpochMilliseconds(endTime, kotlinx.datetime.TimeZone.currentSystemDefault()).toString(),
        timeZoneOffset.toString(),
        value.replace(",", ";").replace("\n", " "),
        numericValue.toString(),
        unit,
        source,
        sourcePackageName,
        sourceRecordId,
        flowLevel,
        testResult,
        temperatureCelsius.toString(),
        mucusConsistency,
        pregnancyTestResult,
        syncStatus,
        lastSyncAttempt.toString(),
        syncError.replace(",", ";").replace("\n", " "),
        conflictResolution,
        createdAt.toString(),
        updatedAt.toString()
    )
}

fun HealthDataPoint.companion.csvHeaders(): List<String> {
    return listOf(
        "id", "userId", "dataType", "startTime", "endTime", "timeZoneOffset",
        "value", "numericValue", "unit", "source", "sourcePackageName", "sourceRecordId",
        "flowLevel", "testResult", "temperatureCelsius", "mucusConsistency",
        "pregnancyTestResult", "syncStatus", "lastSyncAttempt", "syncError",
        "conflictResolution", "createdAt", "updatedAt"
    )
}

fun UserProfile.toJsonMap(anonymize: Boolean): JsonObject {
    val builder = mutableMapOf<String, JsonElement>()
    if (!anonymize) {
        builder.put("id", JsonPrimitive(id))
        builder.put("email", JsonPrimitive(email))
    }
    builder.put("displayName", JsonPrimitive(displayName))
    dateOfBirth?.let { builder.put("dateOfBirth", JsonPrimitive(it.toString())) }
    builder.put("heightCm", JsonPrimitive(heightCm))
    builder.put("weightKg", JsonPrimitive(weightKg))
    builder.put("gender", JsonPrimitive(gender.key))
    builder.put("averageCycleLength", JsonPrimitive(averageCycleLength))
    builder.put("averagePeriodLength", JsonPrimitive(averagePeriodLength))
    builder.put("averageLutealPhaseLength", JsonPrimitive(averageLutealPhaseLength))
    builder.put("cycleRegularity", JsonPrimitive(cycleRegularity.key))
    builder.put("contraceptionMethod", JsonPrimitive(contraceptionMethod.key))
    contraceptionStartDate?.let { builder.put("contraceptionStartDate", JsonPrimitive(it.toString())) }
    lastPeriodDate?.let { builder.put("lastPeriodDate", JsonPrimitive(it.toString())) }
    builder.put("periodDurationDays", JsonPrimitive(periodDurationDays))
    builder.put("pregnancyStatus", JsonPrimitive(pregnancyStatus.key))
    pregnancyDueDate?.let { builder.put("pregnancyDueDate", JsonPrimitive(it.toString())) }
    pregnancyStartDate?.let { builder.put("pregnancyStartDate", JsonPrimitive(it.toString())) }
    builder.put("childrenCount", JsonPrimitive(childrenCount))
    lastPregnancyOutcome?.let { builder.put("lastPregnancyOutcome", JsonPrimitive(it.key)) }
    builder.put("hasPcos", JsonPrimitive(hasPcos))
    builder.put("hasEndometriosis", JsonPrimitive(hasEndometriosis))
    builder.put("hasThyroidCondition", JsonPrimitive(hasThyroidCondition))
    builder.put("hasDiabetes", JsonPrimitive(hasDiabetes))
    builder.put("medicalConditions", JsonPrimitive(medicalConditions.joinToString(";")))
    builder.put("periodReminderEnabled", JsonPrimitive(periodReminderEnabled))
    builder.put("periodReminderDaysBefore", JsonPrimitive(periodReminderDaysBefore))
    builder.put("fertileWindowReminderEnabled", JsonPrimitive(fertileWindowReminderEnabled))
    builder.put("ovulationReminderEnabled", JsonPrimitive(ovulationReminderEnabled))
    builder.put("pillReminderEnabled", JsonPrimitive(pillReminderEnabled))
    builder.put("pillReminderTime", JsonPrimitive(pillReminderTime))
    builder.put("themeMode", JsonPrimitive(themeMode.key))
    builder.put("language", JsonPrimitive(language))
    builder.put("temperatureUnit", JsonPrimitive(temperatureUnit.key))
    builder.put("weightUnit", JsonPrimitive(weightUnit.key))
    builder.put("heightUnit", JsonPrimitive(heightUnit.key))
    builder.put("firstDayOfWeek", JsonPrimitive(firstDayOfWeek))
    builder.put("calendarViewMode", JsonPrimitive(calendarViewMode.key))
    builder.put("analyticsOptIn", JsonPrimitive(analyticsOptIn))
    builder.put("crashReportingOptIn", JsonPrimitive(crashReportingOptIn))
    builder.put("marketingOptIn", JsonPrimitive(marketingOptIn))
    builder.put("healthConnectEnabled", JsonPrimitive(healthConnectEnabled))
    builder.put("healthConnectPermissionsGranted", JsonPrimitive(healthConnectPermissionsGranted))
    builder.put("dataSharingConsentVersion", JsonPrimitive(dataSharingConsentVersion))
    builder.put("dataSharingConsentDate", JsonPrimitive(dataSharingConsentDate))
    builder.put("isAnonymousMode", JsonPrimitive(isAnonymousMode))
    builder.put("accountCreatedAt", JsonPrimitive(accountCreatedAt))
    builder.put("lastLoginAt", JsonPrimitive(lastLoginAt))
    builder.put("emailVerified", JsonPrimitive(emailVerified))
    builder.put("phoneVerified", JsonPrimitive(phoneVerified))
    builder.put("biometricEnabled", JsonPrimitive(biometricEnabled))
    builder.put("autoLockEnabled", JsonPrimitive(autoLockEnabled))
    builder.put("autoLockTimeoutMinutes", JsonPrimitive(autoLockTimeoutMinutes))
    builder.put("subscriptionStatus", JsonPrimitive(subscriptionStatus.key))
    builder.put("subscriptionProductId", JsonPrimitive(subscriptionProductId))
    builder.put("subscriptionExpiresAt", JsonPrimitive(subscriptionExpiresAt))
    builder.put("subscriptionAutoRenew", JsonPrimitive(subscriptionAutoRenew))
    builder.put("subscriptionPlatform", JsonPrimitive(subscriptionPlatform))
    builder.put("trialUsed", JsonPrimitive(trialUsed))
    builder.put("partnerModeEnabled", JsonPrimitive(partnerModeEnabled))
    builder.put("partnerId", JsonPrimitive(partnerId))
    builder.put("partnerShareLevel", JsonPrimitive(partnerShareLevel.key))
    builder.put("partnerInvitationSent", JsonPrimitive(partnerInvitationSent))
    builder.put("partnerInvitationAcceptedAt", JsonPrimitive(partnerInvitationAcceptedAt))
    builder.put("autoSyncEnabled", JsonPrimitive(autoSyncEnabled))
    builder.put("syncFrequencyMinutes", JsonPrimitive(syncFrequencyMinutes))
    builder.put("lastBackupAt", JsonPrimitive(lastBackupAt))
    builder.put("backupFrequencyDays", JsonPrimitive(backupFrequencyDays))
    builder.put("exportFormat", JsonPrimitive(exportFormat.key))
    builder.put("createdAt", JsonPrimitive(createdAt))
    builder.put("updatedAt", JsonPrimitive(updatedAt))
    return JsonObject(builder)
}

fun UserProfile.toCsvRow(anonymize: Boolean): List<String> {
    return listOf(
        if (anonymize) "" else id,
        if (anonymize) "" else email,
        displayName,
        dateOfBirth?.toString() ?: "",
        heightCm.toString(),
        weightKg.toString(),
        gender.key,
        averageCycleLength.toString(),
        averagePeriodLength.toString(),
        averageLutealPhaseLength.toString(),
        cycleRegularity.key,
        contraceptionMethod.key,
        contraceptionStartDate?.toString() ?: "",
        lastPeriodDate?.toString() ?: "",
        periodDurationDays.toString(),
        pregnancyStatus.key,
        pregnancyDueDate?.toString() ?: "",
        pregnancyStartDate?.toString() ?: "",
        childrenCount.toString(),
        lastPregnancyOutcome?.key ?: "",
        hasPcos.toString(),
        hasEndometriosis.toString(),
        hasThyroidCondition.toString(),
        hasDiabetes.toString(),
        medicalConditions.joinToString(";"),
        periodReminderEnabled.toString(),
        periodReminderDaysBefore.toString(),
        fertileWindowReminderEnabled.toString(),
        ovulationReminderEnabled.toString(),
        pillReminderEnabled.toString(),
        pillReminderTime,
        themeMode.key,
        language,
        temperatureUnit.key,
        weightUnit.key,
        heightUnit.key,
        firstDayOfWeek.toString(),
        calendarViewMode.key,
        analyticsOptIn.toString(),
        crashReportingOptIn.toString(),
        marketingOptIn.toString(),
        healthConnectEnabled.toString(),
        healthConnectPermissionsGranted.toString(),
        dataSharingConsentVersion,
        dataSharingConsentDate.toString(),
        isAnonymousMode.toString(),
        accountCreatedAt.toString(),
        lastLoginAt.toString(),
        emailVerified.toString(),
        phoneVerified.toString(),
        biometricEnabled.toString(),
        autoLockEnabled.toString(),
        autoLockTimeoutMinutes.toString(),
        subscriptionStatus.key,
        subscriptionProductId,
        subscriptionExpiresAt.toString(),
        subscriptionAutoRenew.toString(),
        subscriptionPlatform,
        trialUsed.toString(),
        partnerModeEnabled.toString(),
        partnerId,
        partnerShareLevel.key,
        partnerInvitationSent.toString(),
        partnerInvitationAcceptedAt.toString(),
        autoSyncEnabled.toString(),
        syncFrequencyMinutes.toString(),
        lastBackupAt.toString(),
        backupFrequencyDays.toString(),
        exportFormat.key,
        createdAt.toString(),
        updatedAt.toString()
    )
}

fun UserProfile.companion.csvHeaders(): List<String> {
    return listOf(
        "id", "email", "displayName", "dateOfBirth", "heightCm", "weightKg", "gender",
        "averageCycleLength", "averagePeriodLength", "averageLutealPhaseLength",
        "cycleRegularity", "contraceptionMethod", "contraceptionStartDate", "lastPeriodDate",
        "periodDurationDays", "pregnancyStatus", "pregnancyDueDate", "pregnancyStartDate",
        "childrenCount", "lastPregnancyOutcome", "hasPcos", "hasEndometriosis",
        "hasThyroidCondition", "hasDiabetes", "medicalConditions",
        "periodReminderEnabled", "periodReminderDaysBefore", "fertileWindowReminderEnabled",
        "ovulationReminderEnabled", "pillReminderEnabled", "pillReminderTime",
        "themeMode", "language", "temperatureUnit", "weightUnit", "heightUnit",
        "firstDayOfWeek", "calendarViewMode", "analyticsOptIn", "crashReportingOptIn",
        "marketingOptIn", "healthConnectEnabled", "healthConnectPermissionsGranted",
        "dataSharingConsentVersion", "dataSharingConsentDate", "isAnonymousMode",
        "accountCreatedAt", "lastLoginAt", "emailVerified", "phoneVerified",
        "biometricEnabled", "autoLockEnabled", "autoLockTimeoutMinutes",
        "subscriptionStatus", "subscriptionProductId", "subscriptionExpiresAt",
        "subscriptionAutoRenew", "subscriptionPlatform", "trialUsed",
        "partnerModeEnabled", "partnerId", "partnerShareLevel",
        "partnerInvitationSent", "partnerInvitationAcceptedAt",
        "autoSyncEnabled", "syncFrequencyMinutes", "lastBackupAt",
        "backupFrequencyDays", "exportFormat", "createdAt", "updatedAt"
    )
}

fun Subscription.toJsonMap(anonymize: Boolean): JsonObject {
    val builder = mutableMapOf<String, JsonElement>()
    if (!anonymize) {
        builder.put("id", JsonPrimitive(id))
        builder.put("userId", JsonPrimitive(userId))
    }
    builder.put("productId", JsonPrimitive(productId))
    builder.put("productType", JsonPrimitive(productType))
    builder.put("basePlanId", JsonPrimitive(basePlanId))
    builder.put("offerId", JsonPrimitive(offerId))
    builder.put("purchaseToken", JsonPrimitive(purchaseToken))
    builder.put("orderId", JsonPrimitive(orderId))
    builder.put("packageName", JsonPrimitive(packageName))
    builder.put("status", JsonPrimitive(status.key))
    builder.put("purchaseState", JsonPrimitive(purchaseState.value))
    builder.put("purchaseTime", JsonPrimitive(purchaseTime))
    builder.put("expiryTime", JsonPrimitive(expiryTime))
    builder.put("autoRenewing", JsonPrimitive(autoRenewing))
    builder.put("gracePeriodExpiryTime", JsonPrimitive(gracePeriodExpiryTime))
    builder.put("accountHoldExpiryTime", JsonPrimitive(accountHoldExpiryTime))
    builder.put("isTrial", JsonPrimitive(isTrial))
    builder.put("trialStartTime", JsonPrimitive(trialStartTime))
    builder.put("trialEndTime", JsonPrimitive(trialEndTime))
    builder.put("priceAmountMicros", JsonPrimitive(priceAmountMicros))
    builder.put("priceCurrencyCode", JsonPrimitive(priceCurrencyCode))
    builder.put("priceFormatted", JsonPrimitive(priceFormatted))
    builder.put("platform", JsonPrimitive(platform.key))
    builder.put("platformData", JsonPrimitive(platformData))
    builder.put("acknowledged", JsonPrimitive(acknowledged))
    builder.put("acknowledgedAt", JsonPrimitive(acknowledgedAt))
    builder.put("lastNotificationType", JsonPrimitive(lastNotificationType))
    builder.put("lastNotificationTime", JsonPrimitive(lastNotificationTime))
    builder.put("createdAt", JsonPrimitive(createdAt))
    builder.put("updatedAt", JsonPrimitive(updatedAt))
    return JsonObject(builder)
}

fun Subscription.toCsvRow(anonymize: Boolean): List<String> {
    return listOf(
        if (anonymize) "" else id,
        if (anonymize) "" else userId,
        productId,
        productType,
        basePlanId,
        offerId,
        purchaseToken,
        orderId,
        packageName,
        status.key,
        purchaseState.value.toString(),
        purchaseTime.toString(),
        expiryTime.toString(),
        autoRenewing.toString(),
        gracePeriodExpiryTime.toString(),
        accountHoldExpiryTime.toString(),
        isTrial.toString(),
        trialStartTime.toString(),
        trialEndTime.toString(),
        priceAmountMicros.toString(),
        priceCurrencyCode,
        priceFormatted,
        platform.key,
        platformData,
        acknowledged.toString(),
        acknowledgedAt.toString(),
        lastNotificationType,
        lastNotificationTime.toString(),
        createdAt.toString(),
        updatedAt.toString()
    )
}

fun Subscription.companion.csvHeaders(): List<String> {
    return listOf(
        "id", "userId", "productId", "productType", "basePlanId", "offerId",
        "purchaseToken", "orderId", "packageName", "status", "purchaseState",
        "purchaseTime", "expiryTime", "autoRenewing", "gracePeriodExpiryTime",
        "accountHoldExpiryTime", "isTrial", "trialStartTime", "trialEndTime",
        "priceAmountMicros", "priceCurrencyCode", "priceFormatted", "platform",
        "platformData", "acknowledged", "acknowledgedAt", "lastNotificationType",
        "lastNotificationTime", "createdAt", "updatedAt"
    )
}