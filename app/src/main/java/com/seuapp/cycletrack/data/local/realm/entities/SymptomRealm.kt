package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class SymptomRealm : RealmObject() {
    @PrimaryKey
    @Required
    var id: String = ""

    @Index
    @Required
    var userId: String = ""

    @Index
    var date: Long = 0L

    var category: String = ""

    var name: String = ""

    var severity: Int = 2

    var notes: String = ""

    var value: Double? = null

    var createdAt: Long = System.currentTimeMillis()

    var deletedAt: Long = 0L
}
