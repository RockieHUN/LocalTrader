package com.example.localtrader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.localtrader.authentication.AuthActivity
import com.example.localtrader.authentication.models.User
import com.example.localtrader.databinding.ActivityMainBinding
import com.example.localtrader.viewmodels.UserViewModel
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val userViewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val navController = findNavController(R.id.fragment)
        val bottomNav = binding.bottomNavigationView
        bottomNav.setupWithNavController(navController)


        userViewModel.user.value = intent.getSerializableExtra("user") as User
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser == null){
            finishActivity()
        }
    }

    fun finishActivity(){
        auth.signOut()
        val intent = Intent(this.baseContext, AuthActivity :: class.java)
        startActivity(intent)
        finish()
    }


}