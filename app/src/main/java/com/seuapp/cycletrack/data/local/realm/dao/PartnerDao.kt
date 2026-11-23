package com.seuapp.cycletrack.data.local.realm.dao

import com.seuapp.cycletrack.data.local.realm.entities.PartnerRealm
import io.realm.Realm
import io.realm.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PartnerDao @Inject constructor() {

    private val realm: Realm
        get() = Realm.getDefaultInstance()

    suspend fun getActivePartner(userId: String): PartnerRealm? = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(PartnerRealm::class.java)
                .equalTo("userId", userId)
                .sort("createdAt", Sort.DESCENDING)
                .findFirst()
                ?.let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPartners(userId: String): List<PartnerRealm> = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(PartnerRealm::class.java)
                .equalTo("userId", userId)
                .sort("createdAt", Sort.DESCENDING)
                .findAll()
                .let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPartner(partnerId: String): PartnerRealm? = withContext(Dispatchers.IO) {
        try {
            val r = realm
            r.refresh()
            r.where(PartnerRealm::class.java)
                .equalTo("id", partnerId)
                .findFirst()
                ?.let { r.copyFromRealm(it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun upsertPartner(partner: PartnerRealm) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction { it.insertOrUpdate(partner) }
        } catch (e: Exception) {
            // Falha silenciosa: persistencia local e best-effort
        }
    }

    suspend fun deletePartner(partnerId: String) = withContext(Dispatchers.IO) {
        try {
            realm.executeTransaction { r ->
                r.where(PartnerRealm::class.java).equalTo("id", partnerId).findFirst()?.deleteFromRealm()
            }
        } catch (e: Exception) {
            // Falha silenciosa: persistencia local e best-effort
        }
    }

    suspend fun hardDeletePartner(partnerId: String) = deletePartner(partnerId)
}
