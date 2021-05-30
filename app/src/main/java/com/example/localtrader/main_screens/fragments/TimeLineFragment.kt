package com.example.localtrader.main_screens.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.localtrader.NoticeDialog
import com.example.localtrader.R
import com.example.localtrader.authentication.models.User
import com.example.localtrader.business.models.Business
import com.example.localtrader.databinding.FragmentTimeLineBinding
import com.example.localtrader.location.models.MyLocation
import com.example.localtrader.location.PermissionRequests
import com.example.localtrader.main_screens.adapters.PopularProductsAdapter
import com.example.localtrader.main_screens.adapters.LocalBusinessesAdapter
import com.example.localtrader.main_screens.repositories.TimeLineViewModel
import com.example.localtrader.product.models.Product
import com.example.localtrader.viewmodels.*
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask

class TimeLineFragment : Fragment(),
    PopularProductsAdapter.OnItemClickListener,
    LocalBusinessesAdapter.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener
     {
         private lateinit var binding: FragmentTimeLineBinding
         private val userViewModel: UserViewModel by activityViewModels()
         private val businessViewModel: BusinessViewModel by activityViewModels()
         private val productViewModel: ProductViewModel by activityViewModels()
         private val navigationViewModel: NavigationViewModel by activityViewModels()
         private val timelineViewModel: TimeLineViewModel by activityViewModels()

         private val locationRequestCode = 1000
         private val deviceLocation: MutableLiveData<Location?> = MutableLiveData()

         private val auth = Firebase.auth
         private val storage = Firebase.storage
         private val profileImagePath ="users/${auth.currentUser!!.uid}/profilePicture/PROFILE_IMAGE_300"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigationViewModel.origin = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_line, container, false)
        showUserData()
        setGreeting()

        setUpVisuals()
        setUpListeners()

        recyclePopularProducts()
        locationData()
        recycleLocalBusinesses()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        showUserData()
    }

    private fun setUpListeners() {
        var callbackCounter = 0
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (callbackCounter == 0) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.press_again_to_exit),
                    Toast.LENGTH_SHORT
                ).show()
                Timer().schedule(timerTask {
                    callbackCounter = 0
                }, 2000)
                callbackCounter++
            } else requireActivity().finish()
        }

        binding.profilePicture.setOnClickListener {
            findNavController().navigate(R.id.action_timeLineFragment_to_profileFragment)
        }

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    // ------------------------------------------------- LOCATION ---------------------------------------------------

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationRequestCode) {
            if (grantResults[0] == 0 && grantResults[1] == 0) {
                saveLocation()
            } else {
                deviceLocation.value = null
            }
        }
    }

    private fun locationData() {
        saveLocation()
    }



    private fun saveLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionRequests.requestLocationPermissionFragment(this, locationRequestCode)
        } else {
            val locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    deviceLocation.value = location
                    userViewModel.saveLocationData(
                        location.longitude,
                        location.latitude,
                        auth.currentUser!!.uid
                    )
                } else {
                    deviceLocation.value = null
                }
            }
        }
    }

    // ---------------------------------------------------- FEED - Recycle Views --------------------------------------------

    //set recommended products recycle view
    private fun recyclePopularProducts() {
        val adapter = PopularProductsAdapter(this, requireActivity(), listOf())
        binding.recycleRecommendedProducts.adapter = adapter
        val horizontalLayout = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recycleRecommendedProducts.layoutManager = horizontalLayout
        binding.recycleRecommendedProducts.setHasFixedSize(true)

        timelineViewModel.popularProducts.observe(viewLifecycleOwner, { products ->
            adapter.updateData(products)
        })

        lifecycleScope.launch {
            timelineViewModel.getPopularProducts()
        }
    }

    //set local businesses recycle view
    private fun recycleLocalBusinesses() {
        val adapter = LocalBusinessesAdapter(this, listOf(), requireActivity())

        binding.recycleLocalBusinesses.adapter = adapter
        val horizontalLayout = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recycleLocalBusinesses.layoutManager = horizontalLayout
        binding.recycleLocalBusinesses.setHasFixedSize(true)

        timelineViewModel.localBusinesses.observe(viewLifecycleOwner, { businesses ->
            adapter.updateData(businesses)
            binding.recycleLocalBusinesses.scrollToPosition(0)
        })

        deviceLocation.observe(viewLifecycleOwner, {
            var location: MyLocation? = null
            if (it != null) {
                location = MyLocation(longitude = it.longitude, latitude = it.latitude)
            }

            lifecycleScope.launch {
                timelineViewModel.getLocalBusinesses(location, requireContext())
            }
        })

    }

    private fun setUpVisuals() {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }

    private fun showUserData() {

        storage.reference.child(profileImagePath)
            .downloadUrl
            .addOnSuccessListener{ uri ->
                Glide.with(requireContext())
                    .load(uri)
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .circleCrop()
                    .into(binding.profilePicture)
            }

        userViewModel.user.observe(viewLifecycleOwner, object : Observer<User?> {
            override fun onChanged(user: User?) {
                binding.lastName.text = user!!.lastname
                userViewModel.user.removeObserver(this)
            }
        })
    }

    private fun setGreeting() {
        val greeting = binding.greeting
        val time = LocalDateTime.now()

        val minute = time.hour * 60 + time.minute

        if (minute in 300..719) {
            greeting.text = resources.getText(R.string.timeline_greeting1)
        } else if (minute in 720..1019) {
            greeting.text = resources.getText(R.string.timeline_greeting2)
        } else if (minute > 1019 || minute < 300) {
            greeting.text = resources.getText(R.string.timeline_greeting3)
        }
    }

    override fun onItemClick(position: Int) {
        return
    }

    override fun myPopProductOnItemClick(product: Product) {
        productViewModel.product = product
        findNavController().navigate(R.id.action_timeLineFragment_to_productProfileFragment)
    }

    override fun myRecBusinessOnItemClick(business: Business) {
        businessViewModel.businessOwner = business.ownerUid
        businessViewModel.businessId = business.businessId
        businessViewModel.originFragment = 2
        findNavController().navigate(R.id.action_timeLineFragment_to_businessProfileFragment)
    }

     override fun onRefresh() {

         timelineViewModel.loadedComponents.observe(viewLifecycleOwner,{ loadedComponents ->
             if (loadedComponents[0] && loadedComponents[1]){
                 binding.swipeRefresh.isRefreshing = false
             }
         })

         lifecycleScope.launch {
             timelineViewModel.switchAllToLoadable()
             timelineViewModel.getPopularProducts()

             if (deviceLocation.value != null){
                 val location = MyLocation(deviceLocation.value!!.longitude, deviceLocation.value!!.latitude)
                 timelineViewModel.getLocalBusinesses(location, requireContext())
             }
         }
     }

}