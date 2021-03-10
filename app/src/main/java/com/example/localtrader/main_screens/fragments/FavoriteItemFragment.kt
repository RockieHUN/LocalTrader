package com.example.localtrader.main_screens.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.YesNoDialogFragment
import com.example.localtrader.databinding.FragmentFavoriteItemBinding
import com.example.localtrader.product.models.Product
import com.example.localtrader.repositories.FavoritesRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FavoriteItemFragment( val product : Product) : Fragment(),
 YesNoDialogFragment.NoticeDialogListener{

    private lateinit var binding : FragmentFavoriteItemBinding
    private lateinit var storage: FirebaseStorage

    private lateinit var favoritesRepository: FavoritesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_item, container, false)

        favoritesRepository = FavoritesRepository(context, binding.favoriteButton)
        setUpVisuals()
        setData()
        setUpListeners()

        return binding.root
    }

    private fun setUpListeners() {
        binding.favoriteButton.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun setUpVisuals(){
        binding.favoriteButton.setColorFilter(Color.argb(255, 237, 55, 115))
    }

    private fun showAlertDialog(){
        val dialog = YesNoDialogFragment(resources.getString(R.string.delete_favorite),this)
        dialog.show(requireActivity().supportFragmentManager, null)
    }

    private fun setData(){

        binding.price.text = product.price.toString()
        binding.productDescription.text = product.description

        storage.reference.child("products/${product.productId}/image")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(requireActivity())
                    .load(uri)
                    .centerCrop()
                    .into(binding.productImage)
            }
    }

    //TODO: reload data
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        favoritesRepository.removeFromFavorites(product)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        return
    }


}