package com.example.localtrader.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.product.models.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class ProductViewModel : ViewModel() {

    val businessProducts : MutableLiveData<MutableList<Product>> = MutableLiveData()
    var product : Product = Product()

    private val firestore = Firebase.firestore

    fun loadBusinessProducts(businessId : String){
        firestore.collection("products")
            .whereEqualTo("businessId",businessId)
            //.whereNotEqualTo("deleted", "false")
            .get()
            .addOnSuccessListener { documents ->
                businessProducts.value = documents.toObjects<Product>().toMutableList()
            }
    }
}