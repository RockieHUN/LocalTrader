package com.example.localtrader

import android.app.Activity
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
        createGoogleClient(this).signOut()

        val intent = Intent(this.baseContext, AuthActivity :: class.java)
        startActivity(intent)
        finish()
    }

    private fun createGoogleClient(activity : Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("512088991736-jif4r4sog4jmlbahnkv1i61vtg0sirg1.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()

        return GoogleSignIn.getClient(activity ,gso)
    }


}