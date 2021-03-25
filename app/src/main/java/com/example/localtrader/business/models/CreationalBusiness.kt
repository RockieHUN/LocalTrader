package com.example.localtrader.business.models

import android.net.Uri

data class CreationalBusiness (
    var imageUri : Uri? = null,
    var name : String = "",
    var category : String ="",
    var description : String = "",
    var email : String ="",
    var telephone : String = "",
    var longitude : Double = 0.0,
    var latitude : Double = 0.0,
    var facebook_link : String = "",
    var instagram_link : String= ""
        )