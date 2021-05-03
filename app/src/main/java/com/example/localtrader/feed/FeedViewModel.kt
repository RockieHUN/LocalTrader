package com.example.localtrader.feed

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.feed.models.FeedBusinessItem
import com.example.localtrader.feed.models.FeedItem
import com.example.localtrader.feed.models.FeedLoadItem
import com.example.localtrader.feed.models.FeedNoMoreItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.sync.Mutex
import okhttp3.internal.notify

class FeedViewModel : ViewModel() {

    private val firestore = Firebase.firestore

    val feedItems : MutableLiveData<MutableList<FeedItem>> = MutableLiveData()
    private val businessItems : MutableLiveData<List<FeedBusinessItem>> = MutableLiveData()

    //constants
    private val MAX_NEW_ITEMS : Long = 3
    private val MAX_FEED_ITEMS : Long = 7

    private var nextWasLoaded = false
    private lateinit var businessLastItemId : String
    private val mutex = Mutex()


    fun loadFeed(owner: LifecycleOwner){

        businessItems.observe(owner, { businesses ->
            val list = mutableListOf<FeedItem>()
            businesses.forEach { businessItem ->
                list.add(businessItem)
            }
            list.add(FeedLoadItem())
            feedItems.value = list
        })
        loadBusinesses()
    }

    fun loadPreviousItems(){
        if (mutex.isLocked || !nextWasLoaded) return


        firestore.collection("businesses")
            .orderBy("businessId")
            //.startAt(businessPreviousItemId)
            .limit(MAX_FEED_ITEMS)
            .get()
            .addOnSuccessListener {

            }
    }

    suspend fun loadNextItems(){

        if (mutex.isLocked) return
        mutex.lock(this)

        firestore.collection("businesses")
            .orderBy("businessId")
            .startAfter(businessLastItemId)
            .limit(MAX_NEW_ITEMS)
            .get()
            .addOnSuccessListener { documentList ->

                //convert snapshots to classes
                val newItems = documentList.toObjects<FeedBusinessItem>().toMutableList()

                //if no new items, unlock mutex and return
                if (newItems.isEmpty()){
                    val tempList = feedItems.value!!
                    tempList[tempList.size-1] = FeedNoMoreItem()
                    feedItems.value = tempList
                    mutex.unlock()
                    return@addOnSuccessListener
                }

                //get feed items and remove the loading item from the end of the list
                val newList = feedItems.value
                //newList!!.removeAt(newList.size - 1)
                newList!!.removeLast()

                //add the new items to the feed items
                newItems.forEach { item ->
                    newList!!.add(item)
                }

                //set the last business id
                businessLastItemId = newItems.last().businessId


                //add loading item
                newList!!.add(FeedLoadItem())

                //set the new items and unlock the mutex
                feedItems.value = newList
                mutex.unlock()
            }
    }

    private fun loadBusinesses(){

        firestore.collection("businesses")
            .orderBy("businessId")
            .limit(MAX_FEED_ITEMS)
            .get()
            .addOnSuccessListener { documentList ->
                businessItems.value = documentList.toObjects()
                businessLastItemId =  businessItems.value!!.last().businessId
            }
    }


}