package com.example.localtrader.main_screens.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentFavoritesBinding
import com.example.localtrader.main_screens.adapters.FavoriteItemPagerAdapter
import com.example.localtrader.product.models.LikedProduct
import com.example.localtrader.product.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class FavoritesFragment : Fragment() {

    private lateinit var binding : FragmentFavoritesBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favorites, container, false)
        viewPager()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()

    }

    private fun viewPager(){
        val uid = auth.currentUser!!.uid

        firestore.collection("users")
            .document(uid)
            .collection("likedProducts")
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.toObjects<LikedProduct>()
                val ids = mutableListOf<String>()

                for (item in list){
                    ids.add(item.productId)
                }

                firestore.collection("products")
                    .whereIn("productId",ids)
                    .get()
                    .addOnSuccessListener { productsSnapshot ->
                        val products = productsSnapshot.toObjects<Product>()

                        val adapter = FavoriteItemPagerAdapter(this, products)
                        binding.viewPager.adapter = adapter
                    }
            }
    }

    private fun setUpListeners()
    {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_favoritesFragment_to_timeLineFragment)
        }
    }
}