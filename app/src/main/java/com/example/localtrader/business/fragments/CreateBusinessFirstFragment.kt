package com.example.localtrader.business.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentCreateBusinessFirstBinding

class CreateBusinessFirstFragment : Fragment() {

    private lateinit var binding : FragmentCreateBusinessFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_business_first, container, false)
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
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_createBusinessFirstFragment_to_createBusinessSecondFragment)
        }
    }


}