package com.example.localtrader.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localtrader.authentication.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    val user : MutableLiveData<User?> = MutableLiveData()

    private val firestore = Firebase.firestore
    
    fun saveLocationData(longitude : Double, latitude : Double, uid: String){
        viewModelScope.launch (Dispatchers.IO){

            firestore.collection("users")
                .document(uid)
                .collection("userLocation")
                .document("userLocation")
                .set(mapOf("longitude" to longitude, "latitude" to  latitude))

        }
    }

}