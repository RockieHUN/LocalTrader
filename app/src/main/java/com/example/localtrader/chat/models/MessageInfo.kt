package com.example.localtrader.chat.models

import com.google.firebase.Timestamp

data class MessageInfo (
    var senderName : String = "",
    var senderId : String = "",
    var senderType : Int = 1,
    var last_message : String = "",
    var last_message_date: Timestamp = Timestamp.now()
        )