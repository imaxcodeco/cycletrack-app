package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity for data export requests (LGPD/GDPR compliance).
 * Clean-room implementation with audit trail.
 */
@Serializable
open class ExportRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    // Export configuration
    var format: String = "json" // json, csv, pdf
    var includeCycles: Boolean = true
    var includeSymptoms: Boolean = true
    var includePredictions: Boolean = true
    var includePregnancy: Boolean = true
    var includePartnerData: Boolean = false
    var includeHealthConnectData: Boolean = false
    var includeSettings: Boolean = true
    var includeContent: Boolean = false
    var dateRangeStart: Long = 0L
    var dateRangeEnd: Long = 0L
    var anonymize: Boolean = false

    // Status
    var status: String = "pending" // pending, processing, completed, failed, expired
    var progress: Int = 0 // 0-100

    // Output
    var filePath: String = ""
    var fileSize: Long = 0
    var fileHash: String = "" // SHA-256 for integrity
    var downloadUrl: String = ""
    var expiresAt: Long = 0L

    // Error handling
    var errorMessage: String = ""
    var retryCount: Int = 0

    // Request metadata
    var requestedAt: Long = System.currentTimeMillis()
    var startedAt: Long = 0L
    var completedAt: Long = 0L
    var requestedFrom: String = "app" // app, web, api, support
    var ipAddress: String = ""
    var userAgent: String = ""

    // Compliance
    var legalBasis: String = "user_request" // user_request, legal_obligation, vital_interests
    var retentionDays: Int = 7 // How long to keep the export file

    // Metadata
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deletedAt: Long = 0L
    var serverId: String = ""
    var lastSyncedAt: Long = 0L
    var version: Int = 1

    companion object {
        const val FORMAT_JSON = "json"
        const val FORMAT_CSV = "csv"
        const val FORMAT_PDF = "pdf"

        const val STATUS_PENDING = "pending"
        const val STATUS_PROCESSING = "processing"
        const val STATUS_COMPLETED = "completed"
        const val STATUS_FAILED = "failed"
        const val STATUS_EXPIRED = "expired"

        const val DEFAULT_RETENTION_DAYS = 7
        const val MAX_RETENTION_DAYS = 30
    }
}