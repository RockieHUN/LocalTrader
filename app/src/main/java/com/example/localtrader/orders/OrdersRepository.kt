package com.example.localtrader.orders

import androidx.lifecycle.MutableLiveData
import com.example.localtrader.orders.models.OrderRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class OrdersRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore




    val clientOrders : MutableLiveData<List<OrderRequest>> = MutableLiveData()

    fun loadClientOrders()
    {
        val userId = auth.currentUser?.uid



        if (userId == null) return

        firestore.collection("orderRequests")
            .whereEqualTo("clientId", userId)
            .get()
            .addOnSuccessListener { documents ->
                clientOrders.value = documents.toObjects()
                //Log.d("****",clientOrders.value.toString())
            }
            .addOnFailureListener { e->
                Firebase.crashlytics.log( e.toString())
            }
    }


}