package com.example.localtrader.feed


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    var feedManager : FeedManager = FeedManager.Builder()
        .loadBusinesses()
        .loadAds()
        .build()


    @ExperimentalCoroutinesApi
    fun loadNextItems(){
        viewModelScope.launch {
            feedManager.loadNextItems()
        }
    }


}