package com.example.localtrader.orders.models

import com.example.localtrader.utils.date.MyDateTime
import com.example.localtrader.utils.constants.OrderStatus

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
    var date : String = MyDateTime.getCurrentTime(),
    var status : Int = OrderStatus.WAITING_FOR_CONFIRMATION
        )