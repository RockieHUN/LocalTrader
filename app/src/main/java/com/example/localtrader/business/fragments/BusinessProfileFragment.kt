package com.example.localtrader.business.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.business.adapters.BusinessProfileAdapter
import com.example.localtrader.databinding.FragmentBusinessProfileBinding
import com.example.localtrader.main_screens.adapters.PopularBusinessesAdapter


class BusinessProfileFragment : Fragment(), BusinessProfileAdapter.OnItemClickListener {

    private lateinit var binding : FragmentBusinessProfileBinding
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createRecycle()
        setUpListeners()
    }


    private fun createRecycle()
    {
        val adapter = BusinessProfileAdapter(this)
        binding.recycleView.adapter = adapter
        val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recycleView.layoutManager = horizontalLayout
        binding.recycleView.setHasFixedSize(true)

    }

    private fun setUpListeners()
    {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_businessProfileFragment_to_profileFragment)
        }
    }

    override fun onItemClick(position: Int) {
        return
    }


}