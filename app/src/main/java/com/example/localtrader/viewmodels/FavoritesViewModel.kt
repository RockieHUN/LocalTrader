package com.example.localtrader.viewmodels

import android.content.Context
import android.graphics.Color
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localtrader.R
import com.example.localtrader.product.models.LikedBy
import com.example.localtrader.product.models.LikedProduct
import com.example.localtrader.product.models.Product
import com.example.localtrader.utils.comparators.DateComparator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class FavoritesViewModel : ViewModel() {

    private  var auth = Firebase.auth
    private  var firestore = Firebase.firestore

    val favorites : MutableLiveData<MutableList<Product>> = MutableLiveData()


    fun loadFavorites() {
        val uid = auth.currentUser!!.uid

        //get liked products
        firestore.collection("users")
            .document(uid)
            .collection("likedProducts")
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.toObjects<LikedProduct>().sortedWith(DateComparator)
                val ids = mutableListOf<String>()

                //get the ids and put them to a list
                for (item in list){
                    ids.add(item.productId)
                }

                if (ids.isNotEmpty()){
                    //get products by ids
                    firestore.collection("products")
                        .whereIn("productId",ids)
                        .get()
                        .addOnSuccessListener { productsSnapshot ->
                            var products = productsSnapshot.toObjects<Product>().toMutableList()

                            //sort list by id
                            products = sortById(ids,products)
                            favorites.value = products.toMutableList()
                        }
                }
                else{
                    favorites.value = mutableListOf()
                }

            }
    }

    fun favoritesAction(product : Product, context : Context, favoriteButton : ImageButton)
    {
        firestore.collection("products")
            .document(product.productId)
            .collection("likedBy")
            .whereEqualTo("userId",auth.currentUser!!.uid)
            .limit(1)
            .get()
            .addOnSuccessListener{ documents ->
                if (documents.isEmpty) {
                    addToFavorites(product,context, favoriteButton)
                } else {
                    removeFromFavorites(product, documents, favoriteButton)
                }
            }.addOnFailureListener {e ->
                favoriteButton.colorFilter = null
                Toast.makeText(context, R.string.like_failed, Toast.LENGTH_SHORT).show()
                Firebase.crashlytics.log(e.toString())
            }
    }

    fun removeFromFavorites(product : Product){
        val uid = auth.currentUser!!.uid

        //remove from user
        firestore.collection("users")
            .document(uid)
            .collection("likedProducts")
            .whereEqualTo("productId",product.productId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    document.reference.delete().addOnSuccessListener {
                        //reload favorites
                        loadFavorites()
                    }
                }
            }

        firestore.collection("products")
            .document(product.productId)
            .collection("likedBy")
            .whereEqualTo("userId", uid)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    document.reference.delete()
                }
            }
    }

    fun likeButtonVisual(product: Product, favoriteButton: ImageButton){
        firestore.collection("products")
            .document(product.productId)
            .collection("likedBy")
            .whereEqualTo("userId",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty){
                    favoriteButton.setColorFilter(Color.argb(255, 237, 55, 115))
                }
            }
    }


    private fun addToFavorites(product : Product, context: Context, favoriteButton: ImageButton){
        val uid = auth.currentUser!!.uid
        //set liked
        favoriteButton.setColorFilter(Color.argb(255, 237, 55, 115))

        //set liked in product document
        firestore.collection("products")
            .document(product.productId)
            .collection("likedBy")
            .add(LikedBy(userId = uid))
            .addOnFailureListener { e ->
                favoriteButton.colorFilter = null
                Toast.makeText(context, R.string.like_failed, Toast.LENGTH_SHORT).show()
                Firebase.crashlytics.log(e.toString())
            }

        //set liked in user document
        firestore.collection("users")
            .document(uid)
            .collection("likedProducts")
            .add(LikedProduct(productId = product.productId))
            .addOnFailureListener { e->
                favoriteButton.colorFilter = null
                Toast.makeText(context, R.string.like_failed, Toast.LENGTH_SHORT).show()
                Firebase.crashlytics.log(e.toString())

                //remove from product document
                firestore.collection("products")
                    .document(product.productId)
                    .delete()
            }
    }

    private fun removeFromFavorites(product : Product,documents : QuerySnapshot, favoriteButton: ImageButton){
        val uid = auth.currentUser!!.uid

        for (document in documents) {
            document.reference.delete().addOnSuccessListener {
                favoriteButton.colorFilter = null
            }
        }

        firestore.collection("users")
            .document(uid)
            .collection("likedProducts")
            .whereEqualTo("productId",product.productId)
            .get()
            .addOnSuccessListener { documents2 ->
                for (document in documents2){
                    document.reference.delete()
                }
            }

        favoriteButton.colorFilter = null
    }

    private fun sortById(ids : List<String>, products : MutableList<Product> ) : MutableList<Product>{
        if (ids.size != products.size) return products
        for (i in products.indices){
            val id = ids[i]
            val index = products.indexOfFirst { product -> product.productId == id }
            val temp = products[i]
            products[i] = products[index]
            products[index] = temp
        }
        return products
    }
}