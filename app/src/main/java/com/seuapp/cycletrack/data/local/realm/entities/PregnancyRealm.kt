package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PregnancyRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var userId: String = ""
}
