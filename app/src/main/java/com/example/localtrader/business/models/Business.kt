package com.example.localtrader.business.models

import android.net.Uri

data class Business (
    var imageUri : Uri? = null,
    var name : String = "",
    var category : String ="",
    var description : String = "",
    var email : String ="",
    var telephone : String = "",
    var city : String = "",
    var facebook_link : String = "",
    var instagram_link : String= ""
        )