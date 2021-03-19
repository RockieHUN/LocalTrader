package com.example.localtrader.main_screens.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.localtrader.NoticeDialog
import com.example.localtrader.R
import com.example.localtrader.business.models.Business
import com.example.localtrader.databinding.FragmentTimeLineBinding
import com.example.localtrader.location.PermissionRequests
import com.example.localtrader.main_screens.adapters.PopularProductsAdapter
import com.example.localtrader.main_screens.adapters.RecommendedBusinessesAdapter
import com.example.localtrader.main_screens.repositories.TimeLineRepository
import com.example.localtrader.product.fragments.ProductProfileFragment
import com.example.localtrader.product.models.Product
import com.example.localtrader.viewmodels.*
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask

class TimeLineFragment : Fragment(),
    PopularProductsAdapter.OnItemClickListener,
    RecommendedBusinessesAdapter.OnItemClickListener,
    NoticeDialog.OnDismissListener
{
    private lateinit var binding : FragmentTimeLineBinding
    private lateinit var auth : FirebaseAuth

    private val userViewModel : UserViewModel by activityViewModels()
    private val businessViewModel : BusinessViewModel by activityViewModels()
    private val productViewModel : ProductViewModel by activityViewModels()
    private val navigationViewModel : NavigationViewModel by activityViewModels()
    private lateinit var dataStoreViewModel : DataStoreViewModel

    private lateinit var repository : TimeLineRepository

    private val locationRequestCode = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreViewModel = ViewModelProvider(requireActivity()).get(DataStoreViewModel::class.java)
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
        locationData()
    }

    override fun onResume() {
        super.onResume()
        showUserData()
    }

    override fun onPause() {
        super.onPause()
        userViewModel.removeAllObserver(viewLifecycleOwner)
    }

    private fun setUpListeners() {
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

    // ------------------------------------------------- LOCATION ---------------------------------------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationRequestCode){
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
               saveLocation()
            }
        }
    }

    private fun locationData(){
        /*dataStoreViewModel.locationDialogIsShown.observe(viewLifecycleOwner, { isShowed ->
            if (isShowed){
                saveLocation()
            }
            else{
                val noticeDialog = NoticeDialog(resources.getString(R.string.notification_location_data), this)
                dataStoreViewModel.locationNotificationShowed()
                noticeDialog.show(requireActivity().supportFragmentManager,null)
            }
        })*/
        saveLocation()
    }

    override fun onNoticeDismiss(dialog: DialogFragment) {
        dialog.dismiss()
        saveLocation()
    }

    private fun saveLocation(){

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            PermissionRequests.requestLocationPermission(requireActivity(), locationRequestCode)
        }
        else{
            val locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            locationClient.lastLocation.addOnSuccessListener { location ->
                userViewModel.saveLocationData(location.longitude, location.latitude, auth.currentUser!!.uid)
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

        repository.popularProducts.observe(viewLifecycleOwner, { products ->
            adapter.updateData(products)
        })
        repository.getPopularProducts()
    }

    //set recommended products recycle view
    private fun recycleRecommendedBusinesses() {
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

    private fun setUpVisuals() {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }

    private fun showDialog(){
        val dialog = ProductProfileFragment()
        dialog.show(requireActivity().supportFragmentManager, null)
    }



    private fun showUserData() {
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

    private fun setGreeting() {
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