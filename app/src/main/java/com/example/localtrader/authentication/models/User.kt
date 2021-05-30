package com.example.localtrader.authentication.models

import java.io.Serializable

data class User (
    var firstname : String = "",
    var lastname : String ="",
    var email : String="",
    var businessId : String="",
    var messagingToken : String =""
) : Serializable