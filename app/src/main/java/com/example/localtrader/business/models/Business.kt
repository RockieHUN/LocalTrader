package com.example.localtrader.business.models

import android.net.Uri

class Business (
    var businessId : String ="",
    var ownerUid : String = "",
    var name : String = "",
    var category : String ="",
    var description : String = "",
    var email : String ="",
    var telephone : String = "",
    var location : String = "",
    var facebook_link : String = "",
    var instagram_link : String= "",
    var isPromoted : Boolean = false
)