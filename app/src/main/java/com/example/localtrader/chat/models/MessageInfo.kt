package com.example.localtrader.chat.models

import com.google.firebase.Timestamp

data class MessageInfo (
    var client : String = "",
    var last_message : String = "",
    var last_message_date: Timestamp = Timestamp.now()
        )