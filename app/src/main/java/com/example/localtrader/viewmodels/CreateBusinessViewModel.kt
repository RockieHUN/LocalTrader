package com.example.localtrader.viewmodels

import androidx.lifecycle.ViewModel
import com.example.localtrader.business.models.Business
import com.example.localtrader.business.models.CreationalBusiness

class CreateBusinessViewModel : ViewModel() {
    var business : CreationalBusiness = CreationalBusiness()

    fun convert(uid : String) : Business
    {
        return Business(
            uid,
            business.name,
            business.category,
            business.description,
            business.email,
            business.telephone,
            business.location,
            business.facebook_link,
            business.instagram_link
        )
    }
}