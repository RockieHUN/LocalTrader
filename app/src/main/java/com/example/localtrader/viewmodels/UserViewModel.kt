package com.example.localtrader.viewmodels

import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.authentication.models.User
import com.example.localtrader.business.models.Business
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UserViewModel : ViewModel() {

    val user : MutableLiveData<User?> = MutableLiveData()
    val downloadUri : MutableLiveData<Uri?> = MutableLiveData()
    val userBusiness : MutableLiveData<Business?> = MutableLiveData()

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage


    fun loadUserData(uid : String)
    {
        loadUser(uid)
        loadBusiness(uid)
    }

    private fun loadBusiness(uid : String)
    {
        firestore.collection("businesses")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                userBusiness.value = document.toObject<Business>()
            }
    }

    private fun loadUser(uid : String)
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

    fun removeBusinessObservers(owner : LifecycleOwner)
    {
        userBusiness.removeObservers(owner)
    }

    fun getDownloadUri(uid : String)
    {
        storage.reference.child("users/${uid}/profilePicture")
            .downloadUrl
            .addOnSuccessListener { uri ->
                downloadUri.value = uri
            }
    }



}