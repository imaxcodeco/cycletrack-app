package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SubscriptionRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
}
