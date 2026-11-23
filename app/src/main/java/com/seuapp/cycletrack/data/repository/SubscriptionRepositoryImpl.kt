package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.SubscriptionDao
import com.seuapp.cycletrack.domain.repository.SubscriptionRepository
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) : SubscriptionRepository {
    override suspend fun getActiveSubscription(userId: String) = null
    override suspend fun getSubscriptions(userId: String) = emptyList<Any>()
    override suspend fun upsertSubscription(subscription: Any) {}
    override suspend fun deleteSubscription(id: String) {}
}
