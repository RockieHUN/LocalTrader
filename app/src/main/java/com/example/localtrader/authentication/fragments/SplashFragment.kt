package com.example.localtrader.authentication.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.authentication.AuthActivity
import com.example.localtrader.authentication.models.User
import com.example.localtrader.authentication.viewmodels.AuthViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class SplashFragment : Fragment() {
    private val authViewModel : AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        tryToLogin()
    }

    override fun onPause() {
        super.onPause()
    }


    private fun tryToLogin()
    {

        if (authViewModel.isLoggedIn())
        {
            authViewModel.user.observe(viewLifecycleOwner, object : Observer<User> {
                override fun onChanged(t: User?) {
                    if (t != null){
                            (requireActivity() as AuthActivity).startMainActivity(t)
                    }
                    else{
                        authViewModel.logOut()
                        findNavController().navigate(R.id.action_splashFragment_to_selectAuthMethodFragment)
                    }
                    authViewModel.user.removeObserver(this)
                }
            })
            authViewModel.loadUser()

        }
        else{
            val sharedPref = requireActivity().getSharedPreferences("onBoard", Context.MODE_PRIVATE)
            val played = sharedPref.getBoolean("played",false)

            if (played) findNavController().navigate(R.id.action_splashFragment_to_selectAuthMethodFragment)
            else findNavController().navigate(R.id.action_splashFragment_to_onBoardFragment)
        }
    }

}