package com.example.localtrader.feed


import androidx.lifecycle.MutableLiveData
import com.example.localtrader.feed.models.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resumeWithException

class FeedManager private constructor(
    private val loadAds : Boolean = false,
    private val loadBusinesses : Boolean = false,
    private val loadProducts : Boolean = false
) {
    private val firestore = Firebase.firestore

    private var businessLastItemId : String? = null
    private var productLastItemId : String? = null
    private val mutex : Mutex = Mutex()
    private var newList : MutableList<FeedItem> = mutableListOf()

    private lateinit var feedConfig: FeedConfig

    val feedItems : MutableLiveData<List<FeedItem>> = MutableLiveData()

    @ExperimentalCoroutinesApi
    suspend fun <T> Task<T>.await() : T{
        if (isComplete){
            val e = exception

            return if (e == null){
                if (isCanceled) throw CancellationException("Task $this was cancelled normally.")
                else result
            }
            else {
                throw e
            }
        }

        return suspendCancellableCoroutine { cont ->
            addOnCompleteListener{
                val e = exception
                if (e == null) {
                    if (isCanceled) cont.cancel() else cont.resume(result,e)
                } else {
                    cont.resumeWithException(e)
                }
            }
        }
    }


    init {
        createFeedConfig()
    }


    private suspend fun loadNextBusinesses(itemLimit : Long) : List<FeedBusinessItem>{
        val documents =  firestore.collection("businesses")
            .orderBy("businessId")
            .startAfter(businessLastItemId)
            .limit(itemLimit)
            .get()
            .await()

        val businesses =  documents.toObjects<FeedBusinessItem>()

        if (businesses.isNotEmpty()) businessLastItemId = businesses.last().businessId
        return businesses
    }


    suspend fun loadNextItems(){
        if (mutex.isLocked) return

        mutex.lock()
        resetNewList()

        if (feedItems.value == null){
            load()
        }
        else{
            if (feedItems.value!!.last() is FeedNoMoreItem){
                mutex.unlock()
                return
            }
            else{
                load()
            }
        }
        mutex.unlock()
    }

    private suspend fun load(){
        //addLoadingIndicator()
        val businesses = loadNextBusinesses(feedConfig.businessPerLoad)

        if (businesses.isEmpty()){
            //removeLoadingIndicator()
            addNoMoreItemsIndicator()
        }
        else{
            //removeLoadingIndicator()
            newList.addAll(businesses)
            if (loadAds) newList.add(FeedAdItem())
            feedItems.value = newList
        }
    }

    private fun resetNewList(){
        newList = if (feedItems.value != null){
            feedItems.value!!.toMutableList()
        } else{
            mutableListOf()
        }
    }

    private fun addLoadingIndicator(){
        newList.add(FeedLoadItem())
        feedItems.value = newList
    }

    private fun removeLoadingIndicator(){
        if (newList.last() is FeedLoadItem) newList.removeLast()
    }

    private fun addNoMoreItemsIndicator(){
        newList.add(FeedNoMoreItem())
        feedItems.value = newList
    }

    private fun createFeedConfig(){
        val businessPercentage = 0.3
        val productPercentage = 0.7
        val newItemsPerLoad = 5

        var businessPerLoad = 0
        var productPerLoad  = 0

        val adPerLoad : Int = if (loadAds) 1 else 0

        if (loadBusinesses && loadProducts){
            businessPerLoad = (businessPercentage * newItemsPerLoad).toInt()
            productPerLoad = (productPercentage * newItemsPerLoad).toInt()
        }
        if (loadBusinesses && !loadProducts) {
            businessPerLoad = newItemsPerLoad
            productPerLoad = 0
        }
        if (!loadBusinesses && loadProducts){
            businessPerLoad = 0
            productPerLoad = newItemsPerLoad
        }

        this.feedConfig = FeedConfig(newItemsPerLoad ,adPerLoad ,businessPerLoad.toLong() ,productPerLoad.toLong())
    }

        class Builder{
            private var loadAds = false
            private var loadBusinesses = false
            private var loadProducts = false

            fun loadAds() = apply { this.loadAds = true }
            fun loadBusinesses() = apply { this.loadBusinesses = true }
            fun loadProducts() = apply { this.loadProducts = true }
            fun build() = FeedManager(
                loadAds = loadAds,
                loadBusinesses = loadBusinesses,
                loadProducts = loadProducts
            )

        }

}