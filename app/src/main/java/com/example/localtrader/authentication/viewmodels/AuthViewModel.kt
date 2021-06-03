package com.example.localtrader.authentication.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.authentication.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth


    var user : MutableLiveData<User> = MutableLiveData()
    var googleProfileUri : Uri? = null

    fun loadUser()
    {
        if (auth.currentUser != null){
            firestore.collection("users")
                .document(auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    user.value = document.toObject<User>()
                }
        }
    }

    fun isLoggedIn() : Boolean{
        return auth.currentUser != null
    }

    fun logOut(){
        auth.signOut()
    }
}