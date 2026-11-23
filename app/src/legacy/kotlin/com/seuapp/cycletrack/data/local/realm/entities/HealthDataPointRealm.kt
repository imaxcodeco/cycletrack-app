package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity for Health Connect data points (bidirectional sync).
 * Clean-room implementation with conflict resolution metadata.
 */
@Serializable
open class HealthDataPointRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    @Index
    var dataType: String = "" // menstrual_flow, ovulation_test, basal_body_temperature, cervical_mucus, pregnancy_test, etc.

    @Index
    var startTime: Long = 0L

    var endTime: Long = 0L

    var timeZoneOffset: Int = 0 // Minutes from UTC

    // Value (type depends on dataType)
    var value: String = "" // JSON string for complex values

    var numericValue: Double = 0.0

    var unit: String = "" // ml, celsius, fahrenheit, boolean, enum_value

    // Source tracking
    var source: String = "app" // app, health_connect, manual, partner
    var sourcePackageName: String = ""
    var sourceRecordId: String = "" // Health Connect record ID

    // Menstrual flow specific
    var flowLevel: String = "" // light, medium, heavy, spotting

    // Ovulation test specific
    var testResult: String = "" // positive, negative, invalid

    // BBT specific
    var temperatureCelsius: Double = 0.0

    // Cervical mucus specific
    var mucusConsistency: String = "" // dry, sticky, creamy, watery, egg_white

    // Pregnancy test specific
    var pregnancyTestResult: String = "" // positive, negative, invalid

    // Sync metadata
    var syncStatus: String = "pending" // pending, synced, conflict, error
    var lastSyncAttempt: Long = 0L
    var syncError: String = ""
    var conflictResolution: String = "" // local_wins, remote_wins, merge, manual

    // Metadata
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    var deletedAt: Long = 0L
    var serverId: String = ""
    var lastSyncedAt: Long = 0L
    var version: Int = 1

    companion object {
        // Health Connect Data Types (clean-room mapping)
        const val TYPE_MENSTRUAL_FLOW = "menstrual_flow"
        const val TYPE_OVULATION_TEST = "ovulation_test"
        const val TYPE_BASAL_BODY_TEMPERATURE = "basal_body_temperature"
        const val TYPE_CERVICAL_MUCUS = "cervical_mucus"
        const val TYPE_PREGNANCY_TEST = "pregnancy_test"
        const val TYPE_INTERCOURSE = "intercourse"
        const val TYPE_CONTRACEPTION = "contraception"
        const val TYPE_VAGINAL_BLEEDING = "vaginal_bleeding"
        const val TYPE_MENSTRUATION = "menstruation"

        // Flow levels
        const val FLOW_SPOTTING = "spotting"
        const val FLOW_LIGHT = "light"
        const val FLOW_MEDIUM = "medium"
        const val FLOW_HEAVY = "heavy"

        // Test results
        const val TEST_POSITIVE = "positive"
        const val TEST_NEGATIVE = "negative"
        const val TEST_INVALID = "invalid"

        // Mucus consistency
        const val MUCUS_DRY = "dry"
        const val MUCUS_STICKY = "sticky"
        const val MUCUS_CREAMY = "creamy"
        const val MUCUS_WATERY = "watery"
        const val MUCUS_EGG_WHITE = "egg_white"

        // Sync status
        const val SYNC_PENDING = "pending"
        const val SYNC_SYNCED = "synced"
        const val SYNC_CONFLICT = "conflict"
        const val SYNC_ERROR = "error"

        // Conflict resolution
        const val CONFLICT_LOCAL_WINS = "local_wins"
        const val CONFLICT_REMOTE_WINS = "remote_wins"
        const val CONFLICT_MERGE = "merge"
        const val CONFLICT_MANUAL = "manual"

        // Sources
        const val SOURCE_APP = "app"
        const val SOURCE_HEALTH_CONNECT = "health_connect"
        const val SOURCE_MANUAL = "manual"
        const val SOURCE_PARTNER = "partner"
    }
}