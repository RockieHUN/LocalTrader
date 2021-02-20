package com.example.localtrader.viewmodels

import androidx.lifecycle.ViewModel
import com.example.localtrader.business.models.Business
import com.example.localtrader.business.models.CreationalBusiness

class CreateBusinessViewModel : ViewModel() {
    var business : CreationalBusiness = CreationalBusiness()

    fun convert(uid : String) : Business
    {
        return Business(
            ownerUid = uid,
            name = business.name,
            category = business.category,
            description = business.description,
            email = business.email,
            telephone = business.telephone,
            location = business.location,
            facebook_link = business.facebook_link,
            instagram_link = business.instagram_link
        )
    }
}