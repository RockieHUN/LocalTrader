package com.example.localtrader.main_screens.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://us-central1-local-trader-2-0.cloudfunctions.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api : ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}