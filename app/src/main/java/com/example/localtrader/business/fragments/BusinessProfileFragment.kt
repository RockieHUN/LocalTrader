package com.example.localtrader.business.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.business.adapters.BusinessProfileAdapter
import com.example.localtrader.chat.models.ChatLoadInformation
import com.example.localtrader.databinding.FragmentBusinessProfileBinding
import com.example.localtrader.product.models.Product
import com.example.localtrader.utils.comparators.DateComparator
import com.example.localtrader.viewmodels.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class BusinessProfileFragment : Fragment(),
    BusinessProfileAdapter.OnItemClickListener,
    BusinessProfileAdapter.OnProductAddedListener
{

    private lateinit var binding : FragmentBusinessProfileBinding
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    private val businessViewModel : BusinessViewModel by activityViewModels()
    private val productViewModel : ProductViewModel by activityViewModels()
    private val navigationViewModel : NavigationViewModel by activityViewModels()
    private val messagesViewModel : MessagesViewModel by activityViewModels()
    private lateinit var uid : String
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationViewModel.origin = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        if (auth.currentUser != null)
        {
            uid = auth.currentUser!!.uid
        }

        hideEditingTools()

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

        binding.moreButton.setOnClickListener {
            findNavController().navigate(R.id.action_businessProfileFragment_to_productGridFragment)
        }

        binding.chatButton.setOnClickListener {
            val chatLoadInformation = ChatLoadInformation(
                businessId = businessViewModel.businessId,
                userId = auth.currentUser!!.uid,
                whoIsTheOther = 2
            )
            messagesViewModel.chatLoadInformation = chatLoadInformation
            findNavController().navigate(R.id.action_businessProfileFragment_to_orderChatFragment)
        }
    }

    private fun hideEditingTools(){
        binding.newProductButton.visibility = View.GONE
        binding.chatButton.visibility = View.GONE
    }

    private fun showEditingTools()
    {
        val business = businessViewModel.business.value!!
        val uid = auth.currentUser!!.uid

        if (business.ownerUid == uid){
            binding.newProductButton.visibility = View.VISIBLE
        }

        if (business.ownerUid != uid){
            binding.chatButton.visibility = View.VISIBLE
        }

    }

    private fun loadBusinessProducts() {
        val businessId = businessViewModel.businessId

        val adapter = BusinessProfileAdapter(this,this,requireActivity(), listOf())
        binding.recycleView.adapter = adapter
        val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recycleView.layoutManager = horizontalLayout
        binding.recycleView.setHasFixedSize(true)

        productViewModel.businessProducts.observe(viewLifecycleOwner,object : Observer<List<Product>>{

            override fun onChanged(productList: List<Product>?) {
                if (productList == null){
                    productViewModel.businessProducts.removeObserver(this)
                    return
                }

                if (productList.isEmpty()){
                    binding.noItemsHolder.visibility = View.VISIBLE
                }
                else{
                    binding.noItemsHolder.visibility = View.GONE
                }

                //sort list by date
                val sortedList = productList.sortedWith(DateComparator).toMutableList()
                adapter.updateData(sortedList)
            }

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

                storage.reference.child("businesses/${businessId}/BUSINESS_IMAGE_720")
                    .downloadUrl
                    .addOnSuccessListener { uri ->
                        Glide.with(requireActivity())
                            .load(uri)
                            .centerCrop()
                            .into(binding.businessProfilePicture)
                    }

                storage.reference.child("businesses/${businessId}/BUSINESS_IMAGE_1080")
                    .downloadUrl
                    .addOnSuccessListener { uri ->
                        Glide.with(requireActivity())
                            .load(uri)
                            .centerCrop()
                            .into(binding.blurImage)
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
            3 -> id = R.id.action_businessProfileFragment_to_feedFragment
            4 -> id = R.id.action_businessProfileFragment_to_searchFragment
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(id)
        }
    }

    override fun onItemClick(position: Int) {
        return
    }

    override fun myOnClickListener(product: Product) {
        productViewModel.product = product
        findNavController().navigate(R.id.action_businessProfileFragment_to_productProfileFragment)
    }

    override fun scrollToPositionZero() {
        binding.recycleView.scrollToPosition(0)
    }
}