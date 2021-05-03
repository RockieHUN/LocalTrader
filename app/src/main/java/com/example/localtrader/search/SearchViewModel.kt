package com.example.localtrader.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localtrader.retrofit.RetrofitInstance
import com.example.localtrader.retrofit.networking.NetworkResponse
import com.example.localtrader.search.models.SearchResult
import com.example.localtrader.search.models.SearchTerm
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    val results : MutableLiveData<List<SearchResult>> = MutableLiveData()
    private var isSearching : Boolean = false

    fun search(text : String, context : Context){

        Log.d("my_search", "trying to search")
        if (isSearching) return


        val searchTerm = SearchTerm( searchTerm = text)

        viewModelScope.launch {

            synchronized(this){
                isSearching = true
                Log.d("my_search", "searching")
            }

            val response = RetrofitInstance.getInstance(context).search(searchTerm)
            if (response is NetworkResponse.Success) {
                results.value = response.body
                Log.d("my_seach", results.value.toString())
            }
            else{
                Log.d("my_error", "search error")
            }

            synchronized(this){
                isSearching = false
                Log.d("my_search", "searching stopped")
            }
        }
    }

}