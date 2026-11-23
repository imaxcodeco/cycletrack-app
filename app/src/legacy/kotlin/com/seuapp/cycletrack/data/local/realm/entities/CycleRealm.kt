package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.serialization.Serializable

/**
 * Realm entity representing a menstrual cycle.
 * Clean-room implementation based on medical standards (WHO, ACOG).
 */
@Serializable
open class CycleRealm : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    @Index
    var startDate: Long = 0L

    var endDate: Long = 0L

    var cycleLength: Int = 0

    var periodLength: Int = 0

    var lutealPhaseLength: Int = 14 // Default per medical literature

    var ovulationDate: Long = 0L

    var fertileWindowStart: Long = 0L

    var fertileWindowEnd: Long = 0L

    var isPredicted: Boolean = false

    var confidenceScore: Float = 0.0f

    var source: String = "manual" // manual, predicted, server, health_connect

    var notes: String = ""

    var symptoms: RealmList<SymptomRealm> = RealmList()

    var createdAt: Long = System.currentTimeMillis()

    var updatedAt: Long = System.currentTimeMillis()

    var deletedAt: Long = 0L // Soft delete

    var serverId: String = "" // For sync

    var lastSyncedAt: Long = 0L

    var version: Int = 1 // Optimistic locking

    companion object {
        const val SOURCE_MANUAL = "manual"
        const val SOURCE_PREDICTED = "predicted"
        const val SOURCE_SERVER = "server"
        const val SOURCE_HEALTH_CONNECT = "health_connect"
    }
}