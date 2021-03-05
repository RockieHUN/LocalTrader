package com.example.localtrader.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.localtrader.MainActivity
import com.example.localtrader.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService : FirebaseMessagingService() {

    private  val CHANNEL_ID = "my_channel"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notificationID = Random.nextInt()

        //create notification manager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //create notification channel if android version >= Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        //create intent
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)  //clear stack until MainActivity is not on top

        //the notification can be used once with FLAG_ONE_SHOT
        val pendingIntent = PendingIntent.getActivity(this,0,intent, FLAG_ONE_SHOT)


        //create notification
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_order_history)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        //show notification
        notificationManager.notify(notificationID, notification)
    }

    private fun createNotificationChannel(notificationManager : NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "Just a description"
            enableLights(true)
            lightColor = Color.GREEN
        }

        notificationManager.createNotificationChannel(channel)
    }

}