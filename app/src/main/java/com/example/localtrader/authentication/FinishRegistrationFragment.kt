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
import com.example.localtrader.databinding.FragmentFinishRegistrationBinding
import java.util.*
import kotlin.concurrent.timerTask

class FinishRegistrationFragment : Fragment() {

    private lateinit var binding : FragmentFinishRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_finish_registration,container,false)
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
        binding.submitButton.setOnClickListener{
            findNavController().navigate(R.id.action_finishRegistrationFragment_to_timeLineFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_finishRegistrationFragment_to_timeLineFragment)
        }
    }
}