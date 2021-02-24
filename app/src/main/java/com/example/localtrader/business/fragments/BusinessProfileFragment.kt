package com.example.localtrader.business.fragments

import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.example.localtrader.R
import com.example.localtrader.business.adapters.BusinessProfileAdapter
import com.example.localtrader.databinding.FragmentBusinessProfileBinding
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.ProductViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class BusinessProfileFragment : Fragment(), BusinessProfileAdapter.OnItemClickListener {

    private lateinit var binding : FragmentBusinessProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage

    private val userViewModel : UserViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()
    private val productViewModel : ProductViewModel by activityViewModels()
    private lateinit var uid : String
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_profile, container, false)
        setBusinessData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        loadBusinessProducts()
        setUpListeners()
    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser == null)
        {
            findNavController().navigate(R.id.action_businessProfileFragment_to_loginFragment)
        }
        else{
            uid = auth.currentUser!!.uid
        }

        hideEditingTools()

    }

    override fun onPause() {
        super.onPause()
        userViewModel.removeBusinessObservers(viewLifecycleOwner)
    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    private fun setUpListeners()
    {
       backNavigation()
        binding.newProductButton.setOnClickListener{
            findNavController().navigate(R.id.action_businessProfileFragment_to_createProductFragment)
        }
    }

    override fun onItemClick(position: Int) {
        return
    }

    private fun hideEditingTools(){
        binding.newProductButton.visibility = View.GONE
    }

    private fun showEditingTools()
    {
        if (businessViewModel.business.value!!.ownerUid == auth.currentUser!!.uid){
            binding.newProductButton.visibility = View.VISIBLE
        }

    }

    private fun loadBusinessProducts() {
        val businessId = businessViewModel.businessId

        productViewModel.businessProducts.observe(viewLifecycleOwner,{ productList ->
            val adapter = BusinessProfileAdapter(this,requireActivity(), productList)
            binding.recycleView.adapter = adapter
            val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.recycleView.layoutManager = horizontalLayout
            binding.recycleView.setHasFixedSize(true)
        })

        productViewModel.loadBusinessProducts(businessId)
    }

    private fun setBusinessData()
    {
        val businessId = businessViewModel.businessId
        businessViewModel.business.observe(viewLifecycleOwner, { business ->
            if (business != null)
            {
                showEditingTools()

                binding.businessName.text = business.name
                binding.businessCategory.text = business.category
                binding.businessDescription.text = business.description

                storage.reference.child("businesses/${businessId}/logo")
                    .downloadUrl
                    .addOnSuccessListener { uri ->

                        Glide.with(requireActivity())
                            .load(uri)
                            .centerCrop()
                            .into(binding.blurImage)

                        Glide.with(requireActivity())
                            .load(uri)
                            .centerCrop()
                            .into(binding.businessProfilePicture)


                    }
            }
        })

        businessViewModel.loadBusiness(businessId)
    }


    private fun backNavigation() {
        val origin = businessViewModel.originFragment
        var id = 0

        when (origin) {
            1 -> id = R.id.action_businessProfileFragment_to_profileFragment
            2 -> id = R.id.action_businessProfileFragment_to_timeLineFragment
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(id)
        }
    }
}