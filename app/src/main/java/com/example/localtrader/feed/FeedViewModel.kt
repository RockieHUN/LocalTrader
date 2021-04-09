package com.example.localtrader.feed

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.feed.models.FeedBusinessItem
import com.example.localtrader.feed.models.FeedItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class FeedViewModel : ViewModel() {

    private val firestore = Firebase.firestore

    val feedItems : MutableLiveData<List<FeedItem>> = MutableLiveData()
    private val businessItems : MutableLiveData<List<FeedBusinessItem>> = MutableLiveData()

    fun loadFeed(owner: LifecycleOwner){

        businessItems.observe(owner, { businesses ->
            feedItems.value = businesses
        })
        loadBusinesses()
    }

    private fun loadBusinesses(){

        firestore.collection("businesses")
            .limit(20)
            .get()
            .addOnSuccessListener { documentList ->
                businessItems.value = documentList.toObjects()
            }
    }
}