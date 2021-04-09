package com.example.localtrader.retrofit

import com.example.localtrader.business.models.Business
import com.example.localtrader.location.models.MyLocation
import com.example.localtrader.retrofit.models.SearchResult
import com.example.localtrader.retrofit.models.SearchTerm
import com.example.localtrader.retrofit.networking.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("recommendations-getLocalBusinesses")
    suspend fun getLocalBusinesses(@Body location : MyLocation) : NetworkResponse<List<Business>, Error>

    @POST("recommendations-getLocalBusinesses")
    suspend fun getLocalBusinesses() : NetworkResponse<List<Business>, Error>

    @POST("search-search")
    suspend fun search(@Body searchTerm : SearchTerm) : NetworkResponse<List<SearchResult>, Error>
}