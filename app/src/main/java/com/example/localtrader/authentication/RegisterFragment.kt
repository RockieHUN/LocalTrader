package com.example.localtrader.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentRegisterBinding
import java.util.*
import kotlin.concurrent.timerTask


class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpVisuals()
        setUpListeners()


    }

    private fun setUpVisuals()
    {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    private fun setUpListeners()
    {
        //navigation
        binding.submitButton.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_finishRegistrationFragment)
        }

        binding.toLogin.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

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


}