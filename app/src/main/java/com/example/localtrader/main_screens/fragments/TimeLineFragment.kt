package com.example.localtrader.main_screens.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentTimeLineBinding
import com.example.localtrader.main_screens.adapters.PopularBusinessesAdapter
import com.example.localtrader.main_screens.adapters.RecommendedProductsAdapter
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask

class TimeLineFragment : Fragment(),
    RecommendedProductsAdapter.OnItemClickListener,
    PopularBusinessesAdapter.OnItemClickListener
{
    private lateinit var binding : FragmentTimeLineBinding
    private lateinit var auth : FirebaseAuth

    private val userViewModel : UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_time_line,container,false)
        setGreeting()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUserData()

        setUpVisuals()
        setUpListeners()

        recycleRecommendedProducts()
        recyclePopularBusinesses()
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
    private fun recycleRecommendedProducts()
    {
        val adapter = RecommendedProductsAdapter(this)
        binding.recycleRecommendedProducts.adapter = adapter
        val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recycleRecommendedProducts.layoutManager = horizontalLayout
        binding.recycleRecommendedProducts.setHasFixedSize(true)
    }

    //set recommended products recycle view
    private fun recyclePopularBusinesses()
    {
        val adapter = PopularBusinessesAdapter(this)
        binding.recyclePopularBusinesses.adapter = adapter
        val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclePopularBusinesses.layoutManager = horizontalLayout
        binding.recyclePopularBusinesses.setHasFixedSize(true)

    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE

    }

    private fun setUpListeners()
    {
        var callbackCounter = 0
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (callbackCounter == 0) {
                Toast.makeText(requireContext(), "Press again to exit", Toast.LENGTH_SHORT).show()
                Timer().schedule(timerTask {
                    callbackCounter = 0
                }, 2000)
                callbackCounter++
            } else requireActivity().finish()
        }

    }

    private fun showUserData()
    {
        userViewModel.downloadUri.observe(viewLifecycleOwner,  { uri ->
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


    override fun onItemClick(position: Int) {
        return
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


}