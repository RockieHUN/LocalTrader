package com.example.localtrader.orders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentOrdersBinding
import com.example.localtrader.orders.adapters.OrdersViewPagerAdapter
import com.example.localtrader.viewmodels.UserViewModel

class OrdersFragment : Fragment(){

    private lateinit var binding : FragmentOrdersBinding

    private val userViewModel : UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)
        setUpVisuals()
        createViewPager()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createViewPager(){
        var count = 2
        if (userViewModel.user.value?.businessId == ""){
            count = 1
        }

        val adapter = OrdersViewPagerAdapter(this, count)
        binding.viewPager.adapter = adapter
    }

    private fun setUpVisuals(){
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }

}