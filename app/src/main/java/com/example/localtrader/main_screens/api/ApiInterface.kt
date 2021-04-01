package com.example.localtrader.main_screens.api

import com.example.localtrader.business.models.Business
import com.example.localtrader.location.MyLocation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("getLocalBusinesses")
    suspend fun getLocalBusinesses(@Body location : MyLocation) : Response<List<Business>>

    @POST("getLocalBusinesses")
    suspend fun getLocalBusinesses() : Response<List<Business>>
}