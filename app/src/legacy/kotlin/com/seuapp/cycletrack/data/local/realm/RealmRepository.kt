package com.seuapp.cycletrack.data.local.realm

import com.seuapp.cycletrack.data.local.realm.dao.*
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Unified Realm Repository providing access to all DAOs.
 * Clean-room implementation managing Realm instance lifecycle.
 *
 * A API Java do Realm e thread-confined: por isso o repositorio e os DAOs obtem a
 * instancia da thread atual via [Realm.getDefaultInstance] em vez de guardar uma
 * instancia fixa criada em outra thread.
 */
class RealmRepository private constructor() {

    val cycles = CycleDao()
    val symptoms = SymptomDao()
    val userProfiles = UserProfileDao()
    val predictions = PredictionDao()
    val pregnancies = PregnancyDao()
    val partners = PartnerDao()
    val subscriptions = SubscriptionDao()
    val content = ContentDao()
    val healthDataPoints = HealthDataPointDao()
    val notifications = NotificationDao()
    val exports = ExportDao()

    /** Instancia do Realm da thread atual. */
    private val realm: Realm
        get() = Realm.getDefaultInstance()

    /**
     * Execute a write transaction.
     */
    suspend fun <T> writeTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        realm.executeTransaction { transactionRealm ->
            block(transactionRealm)
        }
    }

    /**
     * Execute a read transaction.
     */
    suspend fun <T> readTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        realm.executeTransaction { transactionRealm ->
            block(transactionRealm)
        }
    }

    /**
     * Check if the Realm instance of the current thread is closed.
     */
    val isClosed: Boolean
        get() = realm.isClosed

    companion object {
        @Suppress("UNUSED_PARAMETER")
        private var INSTANCE: RealmRepository? = null

        /**
         * Get or create the singleton RealmRepository instance.
         */
        fun getInstance(): RealmRepository = INSTANCE ?: synchronized(this) {
            val repository = RealmRepository()
            INSTANCE = repository
            repository
        }

        /**
         * Create a new RealmRepository instance (for background threads).
         */
        fun createInstance(): RealmRepository = RealmRepository()

        /**
         * Close the singleton instance.
         */
        fun closeInstance() {
            INSTANCE = null
        }
    }
}

/**
 * Extension functions for easier Realm access in coroutines.
 */
suspend fun <T> withRealm(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
    val realm = Realm.getDefaultInstance()
    block(realm)
}

suspend fun <T> withRealmTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
    val realm = Realm.getDefaultInstance()
    var result: T? = null
    realm.executeTransaction { transactionRealm ->
        result = block(transactionRealm)
    }
    result!!
}
