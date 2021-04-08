package com.example.localtrader.main_screens.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.localtrader.business.models.Business
import com.example.localtrader.location.models.MyLocation
import com.example.localtrader.retrofit.NetworkResponse
import com.example.localtrader.retrofit.RetrofitInstance
import com.example.localtrader.product.models.Product
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.lang.Error

class TimeLineRepository (
    private val lifecycleOwner: LifecycleOwner
        ){

    private var firestore : FirebaseFirestore = Firebase.firestore

    val localBusinesses : MutableLiveData<List<Business>> = MutableLiveData()
    val popularProducts : MutableLiveData<MutableList<Product>> = MutableLiveData()


    suspend fun getLocalBusinesses(location : MyLocation?, context : Context) {

        try{
            if (location != null){
                val response =  RetrofitInstance.getInstance(context).getLocalBusinesses(location)
                if (response is NetworkResponse.Success){
                    localBusinesses.value = response.body
                }
            }
            else{
                val response =  RetrofitInstance.getInstance(context).getLocalBusinesses()
                if (response is NetworkResponse.Success){
                    localBusinesses.value = response.body
                }
            }

        }
        catch (e : Error){
            Firebase.crashlytics.log(e.toString())
        }
    }

    fun getPopularProducts(){
        firestore.collection("products")
            .whereNotEqualTo("productId","")
            .limit(6)
            .get()
            .addOnSuccessListener { documents ->
                popularProducts.value = documents.toObjects<Product>().toMutableList()
            }
    }


    fun removeBusinessObservers(owner : LifecycleOwner)
    {
        localBusinesses.removeObservers(owner)
    }




}