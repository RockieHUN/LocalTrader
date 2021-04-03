package com.example.localtrader.chat.models

import com.google.firebase.Timestamp

data class ChatMessage(
    var senderId : String = "",
    var message : String = "",
    var date : Timestamp = Timestamp.now()
)