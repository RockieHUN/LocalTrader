package com.example.localtrader.authentication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.viewmodels.UserViewModel
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


class SplashFragment : Fragment() {
    private lateinit var auth : FirebaseAuth
    private val userViewModel : UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
       // initAdMod()
        tryToLogin()
    }

    override fun onPause() {
        super.onPause()
        userViewModel.removeUserObservers(viewLifecycleOwner)
    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }


    private fun tryToLogin()
    {

        if (auth.currentUser != null)
        {
            userViewModel.user.observe(viewLifecycleOwner, {
                findNavController().navigate(R.id.action_splashFragment_to_timeLineFragment)
            })
            userViewModel.loadUserData(auth.currentUser!!.uid)

        }
        else{
            findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
        }
    }

    private fun initAdMod(){
        MobileAds.initialize(requireContext()) {
            //tryToLogin()
        }
    }

}