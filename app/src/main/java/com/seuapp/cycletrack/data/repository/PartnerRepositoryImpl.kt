package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.PartnerDao
import com.seuapp.cycletrack.data.local.realm.entities.PartnerRealm
import com.seuapp.cycletrack.domain.model.PartnerConnection
import com.seuapp.cycletrack.domain.repository.PartnerRepository
import javax.inject.Inject

class PartnerRepositoryImpl @Inject constructor(
    private val partnerDao: PartnerDao
) : PartnerRepository {

    override suspend fun getActivePartner(userId: String): PartnerConnection? =
        partnerDao.getActivePartner(userId)?.toDomain()

    override suspend fun getPartners(userId: String): List<PartnerConnection> =
        partnerDao.getPartners(userId).map { it.toDomain() }

    override suspend fun getPartner(id: String): PartnerConnection? =
        partnerDao.getPartner(id)?.toDomain()

    override suspend fun upsertPartner(partner: PartnerConnection) {
        partnerDao.upsertPartner(partner.toRealm())
    }

    override suspend fun deletePartner(id: String) {
        partnerDao.deletePartner(id)
    }

    private fun PartnerConnection.toRealm(): PartnerRealm = PartnerRealm().apply {
        this.id = this@toRealm.id
        this.userId = this@toRealm.userId
        this.partnerId = this@toRealm.partnerId
        this.partnerName = this@toRealm.partnerName
        this.inviteCode = this@toRealm.inviteCode
        this.status = this@toRealm.status
        this.shareLevel = this@toRealm.shareLevel
        this.createdAt = this@toRealm.createdAt
    }

    private fun PartnerRealm.toDomain(): PartnerConnection = PartnerConnection(
        id = id,
        userId = userId,
        partnerId = partnerId,
        partnerName = partnerName,
        inviteCode = inviteCode,
        status = status,
        shareLevel = shareLevel,
        createdAt = createdAt
    )
}
