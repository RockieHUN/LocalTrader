package com.example.localtrader.authentication.models

data class RegistrationUser(
    var firstname : String,
    var lastname : String,
    var email : String,
    var password : String,
    var passwordAgain : String
)
