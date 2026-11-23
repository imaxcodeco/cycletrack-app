package com.seuapp.cycletrack.domain.repository

interface SubscriptionRepository {
    suspend fun getActiveSubscription(userId: String): Any?
    suspend fun getSubscriptions(userId: String): List<Any>
    suspend fun upsertSubscription(subscription: Any)
    suspend fun deleteSubscription(id: String)
}
