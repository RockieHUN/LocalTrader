package com.example.localtrader.orders.models

import java.time.LocalDateTime

data class OrderRequest (
    var orderRequestId : String = "",
    var businessId : String = "",
    var clientId : String = "",
    var clientFirstName : String = "",
    var clientLastName : String = "",
    var productId : String = "",
    var productName : String = "",
    var sum : Double = 0.0,
    var count : Int = 1,
    var additionalComment : String = "",
    var date : LocalDateTime = LocalDateTime.now()
        )