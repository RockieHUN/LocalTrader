package com.example.localtrader.orders.models

data class OrderRequest (
    var orderId : String = "",
    var businessId : String = "",
    var clientId : String = "",
    var clientFirstName : String = "",
    var clientLastName : String = "",
    var productId : String = "",
    var productName : String = "",
    var sum : Double = 0.0,
    var count : Int = 1,
    var additionalComment : String = ""
        )