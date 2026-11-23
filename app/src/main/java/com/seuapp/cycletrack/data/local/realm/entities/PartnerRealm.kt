package com.seuapp.cycletrack.data.local.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PartnerRealm : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var userId: String = ""
    var partnerId: String? = null
    var partnerName: String = ""
    var inviteCode: String = ""
    var status: String = "pending"
    var shareLevel: String = "none"
    var createdAt: Long = System.currentTimeMillis()
}
