package com.example.localtrader.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.utils.MySharedPref
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


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
        tryToLogin()
    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }


    private fun tryToLogin()
    {

        if (auth.currentUser != null)
        {
            userViewModel.loadUserData(auth.currentUser!!.uid)
            findNavController().navigate(R.id.action_splashFragment_to_timeLineFragment)
        }
        else{
            findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
        }

        /*
        val credentials = MySharedPref.getFromSharedPref(requireContext())

        if (credentials.containsKey("email") && credentials.containsKey("password"))
        {
            auth.signInWithEmailAndPassword(credentials["email"]!!, credentials["password"]!!)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful)
                    {
                        userViewModel.loadUserData(auth.currentUser!!.uid)

                    }
                    else
                    {
                        findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                    }
                }
        }
        else
        {
            findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
        }*/

    }


}