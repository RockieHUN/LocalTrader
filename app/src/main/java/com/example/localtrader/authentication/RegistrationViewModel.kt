package com.example.localtrader.authentication

import androidx.lifecycle.ViewModel
import com.example.localtrader.authentication.Models.RegistrationUser

class RegistrationViewModel : ViewModel() {

    var registerUser : RegistrationUser?

    init {
        registerUser = null
    }
}