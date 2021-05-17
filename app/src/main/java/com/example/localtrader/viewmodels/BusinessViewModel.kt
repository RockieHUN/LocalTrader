package com.example.localtrader.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.business.models.Business
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class BusinessViewModel : ViewModel() {

    val firestore = Firebase.firestore

    var businessId : String =""
    var businessOwner : String = ""
    val business : MutableLiveData<Business> = MutableLiveData()

    // 1 when coming from profile
    // 2 when coming from timeline
    // 3 when coming from feed
    // 4 when coming from search
    var originFragment : Int = 1

    fun loadBusiness(businessId : String)
    {
        firestore.collection("businesses")
            .document(businessId)
            .get()
            .addOnSuccessListener { document ->
                business.value = document.toObject<Business>()
            }
    }

    fun removeBusinessObservers(owner : LifecycleOwner){
        business.removeObservers(owner)
    }

}







