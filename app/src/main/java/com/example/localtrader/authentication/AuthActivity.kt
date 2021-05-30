package com.example.localtrader.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.localtrader.MainActivity
import com.example.localtrader.R
import com.example.localtrader.authentication.models.User

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        supportActionBar?.hide()
    }

    fun startMainActivity(user : User){
        val intent = Intent(baseContext, MainActivity :: class.java).apply {
            putExtra("user",user)
        }
        startActivity(intent)
        finish()
    }
}