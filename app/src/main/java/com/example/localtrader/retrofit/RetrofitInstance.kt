package com.example.localtrader.retrofit

import android.content.Context
import com.example.localtrader.retrofit.networking.NetworkResponseAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {

    companion object{
        private var INSTANCE : ApiInterface? = null


        fun getInstance(context : Context) : ApiInterface {
            val tempInstance = INSTANCE

            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){

               /* val gson = GsonBuilder()
                    .setLenient()
                    .create() */

                val okHttpClient = OkHttpClient
                    .Builder()
                    //.addInterceptor(NetworkInterceptor(context))
                    .build()

                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(NetworkResponseAdapterFactory())
                    .baseUrl("https://us-central1-local-trader-2-0.cloudfunctions.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()

                val api : ApiInterface = retrofit.create(ApiInterface::class.java)

                INSTANCE = api
                return api
            }
        }
    }

}