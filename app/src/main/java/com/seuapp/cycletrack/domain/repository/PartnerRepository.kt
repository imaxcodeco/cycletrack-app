package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.PartnerConnection

interface PartnerRepository {
    suspend fun getActivePartner(userId: String): PartnerConnection?
    suspend fun getPartners(userId: String): List<PartnerConnection>
    suspend fun getPartner(id: String): PartnerConnection?
    suspend fun upsertPartner(partner: PartnerConnection)
    suspend fun deletePartner(id: String)
}
