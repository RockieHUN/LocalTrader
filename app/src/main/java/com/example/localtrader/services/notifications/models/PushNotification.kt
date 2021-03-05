package com.example.localtrader.services.notifications.models

import com.example.localtrader.services.notifications.models.NotificationData

data class PushNotification (
    val data : NotificationData,
    val to : String
        )