package com.example.localtrader.viewmodels

import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.authentication.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UserViewModel : ViewModel() {

    val user : MutableLiveData<User?> = MutableLiveData()
    val downloadUri : MutableLiveData<Uri?> = MutableLiveData()
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage


    fun loadUserData(uid : String)
    {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                 user.value = document.toObject<User>()
            }

        storage.reference.child("users/${uid}/profilePicture")
            .downloadUrl
            .addOnSuccessListener { uri ->
                downloadUri.value = uri
            }
    }

    fun removeAllObserver(owner : LifecycleOwner)
    {
        user.removeObservers(owner)
        downloadUri.removeObservers(owner)
    }



}