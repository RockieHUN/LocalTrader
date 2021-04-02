package com.example.localtrader.main_screens.repositories

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.localtrader.business.models.Business
import com.example.localtrader.location.models.MyLocation
import com.example.localtrader.main_screens.api.RetrofitInstance
import com.example.localtrader.product.models.Product
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import retrofit2.Response
import java.lang.Error

class TimeLineRepository (
    private val lifecycleOwner: LifecycleOwner
        ){

    private var firestore : FirebaseFirestore = Firebase.firestore

    val localBusinesses : MutableLiveData<List<Business>> = MutableLiveData()
    val popularProducts : MutableLiveData<MutableList<Product>> = MutableLiveData()


    suspend fun getLocalBusinesses(location : MyLocation?) {
        var response : Response<List<Business>>? = null

        try{
            if (location != null){
                response =  RetrofitInstance.api.getLocalBusinesses(location)
            }
            else{
                response = RetrofitInstance.api.getLocalBusinesses()
            }

        }
        catch (e : Error){
            Firebase.crashlytics.log(e.toString())
        }

        if (response != null && response.isSuccessful){
            localBusinesses.value = response.body()
        }
    }

    fun MutableList<Business>.myToString() : String{

        var str = ""
        for (item in this){
            str += item.name + " "
        }
        return str
    }
    
    fun getRecommendedBusinesses()
    {
        val ref = firestore.collection("businesses")
        val key = ref.document().id

        Log.d("********", key)

        val businessList = mutableListOf<Business>()

        //less than key
        ref.whereNotEqualTo("businessId","")
            //.whereEqualTo("isPromoted", true)
            .whereLessThan("businessId",key)
            .limit(6)
            .get()
            .addOnSuccessListener { documents ->

                val list = documents.toObjects<Business>()
                for (business in list){
                    businessList.add(business)
                }
                Log.d("********", businessList.myToString())

                if (list.size < 6)
                {
                    val needed = 6 -list.size
                    ref.whereNotEqualTo("businessId","")
                        //.whereEqualTo("isPromoted", true)
                        .whereGreaterThan("businessId", key)
                        .limit(needed.toLong())
                        .get()
                        .addOnSuccessListener { documents2 ->

                            val list2 = documents2.toObjects<Business>()
                            for (business in list2){
                                businessList.add(business)
                            }
                            Log.d("********", businessList.myToString())
                           // localBusinesses.value = businessList

                        }
                }
                else{
                    //localBusinesses.value = businessList
                }

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