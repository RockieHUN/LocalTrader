package com.example.localtrader.services.notifications

import com.example.localtrader.services.notifications.models.PushNotification
import com.example.localtrader.services.notifications.retrofit.RetrofitInstance
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class NotificationRepository {

    fun sendNotification( notification : PushNotification){

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = RetrofitInstance.api.postNotification(notification)
            }
            catch (e : Exception)
            {
                Firebase.crashlytics.log(e.toString())
            }
        }
    }
}