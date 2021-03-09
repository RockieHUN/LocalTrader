package com.example.localtrader.product.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentProductProfileBinding
import com.example.localtrader.product.models.LikedBy
import com.example.localtrader.product.models.LikedProduct
import com.example.localtrader.product.models.Product
import com.example.localtrader.viewmodels.NavigationViewModel
import com.example.localtrader.viewmodels.ProductViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ProductProfileFragment : DialogFragment() {
    private lateinit var binding : FragmentProductProfileBinding
    private lateinit var storage : FirebaseStorage
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    private val productViewModel : ProductViewModel by activityViewModels()
    private val navigationViewModel : NavigationViewModel by activityViewModels()
    private val userViewModel : UserViewModel by activityViewModels()

    private var isLayerVisible : Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        firestore = Firebase.firestore
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_profile, container, false)

        setUpVisuals()
        setUpListeners()
        setProductData()
        return binding.root
    }

   /* override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.fragment_product_profile, null))

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }*/

    private fun setUpListeners(){
        binding.productImage.setOnClickListener {
            switchLayer()
        }

        binding.orderButton.setOnClickListener {
            this.dismiss()
            when(navigationViewModel.origin){
                1 -> findNavController().navigate(R.id.action_timeLineFragment_to_createOrderFragment)
                2 -> findNavController().navigate(R.id.action_businessProfileFragment_to_createOrderFragment)
                3 -> findNavController().navigate(R.id.action_productGridFragment_to_createOrderFragment)
            }
        }

        binding.favoriteButton.setOnClickListener {
            likeProduct()
        }
    }

    private fun setUpVisuals(){
        if (userViewModel.user.value!!.businessId == productViewModel.product.businessId){
            binding.orderButton.visibility = View.GONE
        }

        likeButtonVisual()
    }

    private fun likeButtonVisual(){
        val product = productViewModel.product
        firestore.collection("products")
            .document(product.productId)
            .collection("likedBy")
            .whereEqualTo("userId",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty){
                    binding.favoriteButton.setColorFilter(Color.argb(255, 237, 55, 115))
                }
            }
    }

    private fun likeProduct()
    {
        val product = productViewModel.product

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
                    removeFromFavorites(documents)
                }
            }.addOnFailureListener {e ->
                binding.favoriteButton.colorFilter = null
                Toast.makeText(context, R.string.like_failed, Toast.LENGTH_SHORT).show()
                Firebase.crashlytics.log(e.toString())
            }
    }

    private fun addToFavorites(product : Product){
        val uid = auth.currentUser!!.uid
        //set liked
        binding.favoriteButton.setColorFilter(Color.argb(255, 237, 55, 115))

        //set liked in product document
        firestore.collection("products")
            .document(product.productId)
            .collection("likedBy")
            .add(LikedBy(userId = uid))
            .addOnFailureListener { e ->
                binding.favoriteButton.colorFilter = null
                Toast.makeText(context, R.string.like_failed, Toast.LENGTH_SHORT).show()
                Firebase.crashlytics.log(e.toString())
            }

        //set liked in user document
        firestore.collection("users")
            .document(uid)
            .collection("likedProducts")
            .add(LikedProduct(productId = product.productId))
            .addOnFailureListener { e->
                binding.favoriteButton.colorFilter = null
                Toast.makeText(context, R.string.like_failed, Toast.LENGTH_SHORT).show()
                Firebase.crashlytics.log(e.toString())

                //remove from product document
                firestore.collection("products")
                    .document(product.productId)
                    .delete()
            }
    }

    private fun removeFromFavorites(documents : QuerySnapshot){
        val uid = auth.currentUser!!.uid
        val product = productViewModel.product

        for (document in documents) {
            document.reference.delete().addOnSuccessListener {
                binding.favoriteButton.colorFilter = null
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

        binding.favoriteButton.colorFilter = null
    }

    private fun setProductData()
    {
        val product = productViewModel.product
        storage.reference.child("products/${product.productId}/image")
            .downloadUrl
            .addOnSuccessListener { uri->
                Glide.with(requireActivity())
                    .load(uri)
                    .centerCrop()
                    .into(binding.productImage)
            }

        binding.productDescription.text = product.description
        binding.price.text = product.price.toString()
    }

    private fun switchLayer(){
        if (isLayerVisible){
            binding.infoLayout.animate()
                .alpha(0f)
                .duration = 200L

            isLayerVisible = false
        }
        else{
            binding.infoLayout.animate()
                .alpha(1f)
                .duration = 200L

            isLayerVisible = true
        }
    }


}