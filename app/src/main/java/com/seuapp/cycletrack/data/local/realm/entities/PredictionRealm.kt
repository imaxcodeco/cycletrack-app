package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PredictionRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var userId: String = ""
    var predictedDate: Long = 0L
    var type: String = ""
}
