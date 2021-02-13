package com.example.localtrader.viewmodels

import androidx.lifecycle.ViewModel
import com.example.localtrader.business.models.Business

class CreateBusinessViewModel : ViewModel() {
    var business : Business = Business()
}