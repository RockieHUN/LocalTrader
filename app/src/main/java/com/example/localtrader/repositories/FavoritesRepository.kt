package com.example.localtrader.repositories

import android.content.Context
import android.graphics.Color
import android.widget.ImageButton
import android.widget.Toast
import com.example.localtrader.R
import com.example.localtrader.product.models.LikedBy
import com.example.localtrader.product.models.LikedProduct
import com.example.localtrader.product.models.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavoritesRepository(
    private val context: Context?,
    private val favoriteButton : ImageButton) {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun favoritesAction(product : Product)
    {
        firestore.collection("products")
            .document(product.productId)
            .collection("likedBy")
            .whereEqualTo("userId",auth.currentUser!!.uid)
            .limit(1)
            .get()
            .addOnSuccessListener{ documents ->
                if (documents.isEmpty) {
                    addToFavorites(product)
                } else {
                    removeFromFavorites(product,documents)
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
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    document.reference.delete()
                }
            }

        firestore.collection("products")
            .document(product.productId)
            .collection("likedBy")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    document.reference.delete()
                }
            }
    }

    fun likeButtonVisual(product: Product){
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


    private fun addToFavorites(product : Product){
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

    private fun removeFromFavorites(product : Product,documents : QuerySnapshot){
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
}