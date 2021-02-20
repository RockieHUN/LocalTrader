package com.example.localtrader.main_screens.repositories

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.localtrader.business.models.Business
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class TimeLineRepository (
    private val lifecycleOwner: LifecycleOwner
        ){

    private var firestore : FirebaseFirestore = Firebase.firestore

    val recommendedBusinesses : MutableLiveData<List<Business>> = MutableLiveData()


    fun getRecommendedBusinesses()
    {
        val ref = firestore.collection("businesses")
        val key = ref.document().id

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

                            recommendedBusinesses.value = businessList
                        }
                }
            }
    }

    fun removeBusinessObservers(owner : LifecycleOwner)
    {
        recommendedBusinesses.removeObservers(owner)
    }




}