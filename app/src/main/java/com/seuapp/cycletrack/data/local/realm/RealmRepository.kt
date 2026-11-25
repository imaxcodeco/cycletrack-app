package com.seuapp.cycletrack.data.local.realm

import com.seuapp.cycletrack.data.local.realm.dao.CycleDao
import com.seuapp.cycletrack.data.local.realm.dao.SymptomDao
import com.seuapp.cycletrack.data.local.realm.dao.UserProfileDao
import com.seuapp.cycletrack.data.local.realm.dao.PredictionDao
import com.seuapp.cycletrack.data.local.realm.dao.PregnancyDao
import com.seuapp.cycletrack.data.local.realm.dao.PartnerDao
import com.seuapp.cycletrack.data.local.realm.dao.ContentDao
import com.seuapp.cycletrack.data.local.realm.dao.HealthDataPointDao
import com.seuapp.cycletrack.data.local.realm.dao.NotificationDao
import com.seuapp.cycletrack.data.local.realm.dao.ExportDao
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealmRepository private constructor() {

    val cycles = CycleDao()
    val symptoms = SymptomDao()
    val userProfiles = UserProfileDao()
    val predictions = PredictionDao()
    val pregnancies = PregnancyDao()
    val partners = PartnerDao()
    val content = ContentDao()
    val healthDataPoints = HealthDataPointDao()
    val notifications = NotificationDao()
    val exports = ExportDao()

    private val realm: Realm
        get() = Realm.getDefaultInstance()

    suspend fun <T> writeTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        var result: T? = null
        realm.executeTransaction { transactionRealm ->
            result = block(transactionRealm)
        }
        result ?: throw IllegalStateException("Transaction block returned null")
    }

    suspend fun <T> readTransaction(block: (Realm) -> T): T = withContext(Dispatchers.IO) {
        var result: T? = null
        realm.executeTransaction { transactionRealm ->
            result = block(transactionRealm)
        }
        result ?: throw IllegalStateException("Transaction block returned null")
    }

    val isClosed: Boolean
        get() = realm.isClosed

    companion object {
        @Suppress("UNUSED_PARAMETER")
        private var INSTANCE: RealmRepository? = null

        fun getInstance(): RealmRepository = INSTANCE ?: synchronized(this) {
            val repository = RealmRepository()
            INSTANCE = repository
            repository
        }

        fun createInstance(): RealmRepository = RealmRepository()

        fun closeInstance() {
            INSTANCE = null
        }
    }
}

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
