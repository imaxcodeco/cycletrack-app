package com.seuapp.cycletrack.data.local.realm.dao

import javax.inject.Inject

class SubscriptionDao @Inject constructor() {
    suspend fun getActiveSubscription(userId: String) = null
    suspend fun getSubscriptions(userId: String) = emptyList<Any>()
    suspend fun getSubscription(subscriptionId: String) = null
    suspend fun upsertSubscription(subscription: Any) {}
    suspend fun deleteSubscription(subscriptionId: String) {}
    suspend fun hardDeleteSubscription(subscriptionId: String) {}
}
