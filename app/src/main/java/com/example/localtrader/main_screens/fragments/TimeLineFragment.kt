package com.example.localtrader.main_screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.business.models.Business
import com.example.localtrader.databinding.FragmentTimeLineBinding
import com.example.localtrader.main_screens.adapters.PopularProductsAdapter
import com.example.localtrader.main_screens.adapters.RecommendedBusinessesAdapter
import com.example.localtrader.main_screens.repositories.TimeLineRepository
import com.example.localtrader.product.fragments.ProductProfileFragment
import com.example.localtrader.product.models.Product
import com.example.localtrader.viewmodels.BusinessViewModel
import com.example.localtrader.viewmodels.NavigationViewModel
import com.example.localtrader.viewmodels.ProductViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask

class TimeLineFragment : Fragment(),
    PopularProductsAdapter.OnItemClickListener,
    RecommendedBusinessesAdapter.OnItemClickListener
{
    private lateinit var binding : FragmentTimeLineBinding
    private lateinit var auth : FirebaseAuth

    private val userViewModel : UserViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()
    private val productViewModel : ProductViewModel by activityViewModels()
    private val navigationViewModel : NavigationViewModel by activityViewModels()

    private lateinit var repository : TimeLineRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        navigationViewModel.origin = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_line, container, false)
        setGreeting()
        repository = TimeLineRepository(viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUserData()

        setUpVisuals()
        setUpListeners()

        recyclePopularProducts()
        recycleRecommendedBusinesses()
    }

    override fun onResume() {
        super.onResume()
        showUserData()
    }

    override fun onPause() {
        super.onPause()
        userViewModel.removeAllObserver(viewLifecycleOwner)
    }

    //set recommended products recycle view
    private fun recyclePopularProducts()
    {
        val adapter = PopularProductsAdapter(this, requireActivity(), listOf())
        binding.recycleRecommendedProducts.adapter = adapter
        val horizontalLayout = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recycleRecommendedProducts.layoutManager = horizontalLayout
        binding.recycleRecommendedProducts.setHasFixedSize(true)

        repository.popularProducts.observe(viewLifecycleOwner, { products ->
            adapter.updateData(products)
        })
        repository.getPopularProducts()
    }

    //set recommended products recycle view
    private fun recycleRecommendedBusinesses()
    {
        val adapter = RecommendedBusinessesAdapter(this, listOf(), requireActivity())
        binding.recyclePopularBusinesses.adapter = adapter
        val horizontalLayout = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclePopularBusinesses.layoutManager = horizontalLayout
        binding.recyclePopularBusinesses.setHasFixedSize(true)

        repository.recommendedBusinesses.observe(viewLifecycleOwner, { businesses ->
           adapter.updateData(businesses)
        })
        repository.getRecommendedBusinesses()
    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }

    private fun showDialog(){
        val dialog = ProductProfileFragment()
        dialog.show(requireActivity().supportFragmentManager, null)
    }

    private fun setUpListeners()
    {
        var callbackCounter = 0
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (callbackCounter == 0) {
                Toast.makeText(requireContext(), resources.getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
                Timer().schedule(timerTask {
                    callbackCounter = 0
                }, 2000)
                callbackCounter++
            } else requireActivity().finish()
        }

        binding.profilePicture.setOnClickListener {
            findNavController().navigate(R.id.action_timeLineFragment_to_profileFragment)
        }
    }

    private fun showUserData()
    {
        userViewModel.downloadUri.observe(viewLifecycleOwner, { uri ->
            Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .circleCrop()
                .into(binding.profilePicture)
        })

        userViewModel.user.observe(viewLifecycleOwner, {
            val lastname = userViewModel.user.value?.lastname

            binding.lastName.text = lastname
        })
    }

    private fun setGreeting()
    {
        val greeting = binding.greeting
        val time = LocalDateTime.now()

        val minute = time.hour * 60 + time.minute

        if (minute in 300..719)
        {
            greeting.text = resources.getText(R.string.timeline_greeting1)
        }
        else if (minute in 720..1019)
        {
            greeting.text = resources.getText(R.string.timeline_greeting2)
        }
        else if (minute > 1019 || minute < 300)
        {
            greeting.text = resources.getText(R.string.timeline_greeting3)
        }
    }

    override fun onItemClick(position: Int) {
        return
    }

    override fun myPopProductOnItemClick(product: Product) {
        productViewModel.product = product
        showDialog()
    }

    override fun myRecBusinessOnItemClick(business: Business) {
        businessViewModel.businessOwner = business.ownerUid
        businessViewModel.businessId = business.businessId
        businessViewModel.originFragment = 2
        findNavController().navigate(R.id.action_timeLineFragment_to_businessProfileFragment)
    }
}