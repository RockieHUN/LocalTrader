package com.example.localtrader.main_screens.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.business.models.Business
import com.example.localtrader.location.models.MyLocation
import com.example.localtrader.retrofit.networking.NetworkResponse
import com.example.localtrader.retrofit.RetrofitInstance
import com.example.localtrader.product.models.Product
import com.example.localtrader.search.models.SearchResult
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Error

class TimeLineViewModel : ViewModel(){

    private var firestore : FirebaseFirestore = Firebase.firestore
    //the 0th value for the businesses,the 1st for the products
    val loadedComponents : MutableLiveData<List<Boolean>> = MutableLiveData(listOf(false, false))
    private val loadMutex = Mutex()


    val localBusinesses : MutableLiveData<List<Business>> = MutableLiveData()
    val popularProducts : MutableLiveData<MutableList<Product>> = MutableLiveData()
    val searchResults : MutableLiveData<List<SearchResult>> = MutableLiveData()


    suspend fun getLocalBusinesses(location : MyLocation?, context : Context) {

        loadMutex.withLock {
            if (loadedComponents.value!![0]) return
        }
        switchBusinessesLoaded()

        try{
            if (location != null){
                val response =  RetrofitInstance.getInstance(context).getLocalBusinesses(location)
                if (response is NetworkResponse.Success){
                    localBusinesses.value = response.body.shuffled()
                }
            }
            else{
                val response =  RetrofitInstance.getInstance(context).getLocalBusinesses()
                if (response is NetworkResponse.Success){
                    localBusinesses.value = response.body.shuffled()
                }
            }

        }
        catch (e : Error){
            Firebase.crashlytics.log(e.toString())
        }
    }

    suspend fun getPopularProducts(){
       if (loadedComponents.value!![1]) return
       switchProductLoaded()

        firestore.collection("products")
            .whereNotEqualTo("productId","")
            .limit(6)
            .get()
            .addOnSuccessListener { documents ->
                popularProducts.value = documents.toObjects<Product>().toMutableList()
            }
    }

   suspend fun switchProductLoaded(){
       loadMutex.withLock {
           loadedComponents.value = listOf(loadedComponents.value!![0], !loadedComponents.value!![1])
       }
   }

    suspend fun switchBusinessesLoaded(){
        loadMutex.withLock {
            loadedComponents.value = listOf(!loadedComponents.value!![0], loadedComponents.value!![1])
        }
    }

    fun switchAllToLoadable(){
        loadedComponents.value = listOf(false, false)
    }

    fun removeBusinessObservers(owner : LifecycleOwner)
    {
        localBusinesses.removeObservers(owner)
    }

}