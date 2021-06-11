package com.example.localtrader.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.ads.MobileAds
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAdMod()
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/myTopic")

        val intent = Intent(this, AuthActivity :: class.java)
        startActivity(intent)
        finish()
    }

    private fun initAdMod(){

        val activity = this
        GlobalScope.launch(Dispatchers.IO) {
            MobileAds.initialize(activity) {

            }
        }

    }



}