package com.example.localtrader.orders.models

import com.google.firebase.Timestamp

data class ChatMessage(
    var senderId : String = "",
    var message : String = "",
    var date : Timestamp = Timestamp.now()
)