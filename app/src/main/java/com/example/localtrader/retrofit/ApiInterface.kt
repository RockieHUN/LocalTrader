package com.example.localtrader.retrofit

import com.example.localtrader.business.models.Business
import com.example.localtrader.location.models.MyLocation
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("recommendations-getLocalBusinesses")
    suspend fun getLocalBusinesses(@Body location : MyLocation) : NetworkResponse<List<Business>, Error>

    @POST("recommendations-getLocalBusinesses")
    suspend fun getLocalBusinesses() : NetworkResponse<List<Business>, Error>
}