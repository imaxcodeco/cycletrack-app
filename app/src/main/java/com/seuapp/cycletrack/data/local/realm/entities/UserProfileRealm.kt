package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserProfileRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var email: String = ""
    var displayName: String = ""
    var goal: String = ""
    var dateOfBirth: Long = 0L
    var averageCycleLength: Int = 28
    var averagePeriodLength: Int = 5
    var lastPeriodStartDate: Long = 0L
    var heightCm: Int = 0
    var weightKg: Float = 0f
    var onboardingCompleted: Boolean = false
    var deletedAt: Long = 0L
}
