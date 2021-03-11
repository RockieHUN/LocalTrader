package com.example.localtrader.product.models

import com.example.localtrader.BaseClass

data class Product (
    var productId : String = "",
    var ownerId : String = "",
    var businessId : String = "",
    var name : String = "",
    var price : Double = 0.0,
    var description : String ="",
    var businessName : String = ""
        ) : BaseClass()