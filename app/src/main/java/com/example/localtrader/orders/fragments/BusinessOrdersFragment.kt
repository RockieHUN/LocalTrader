package com.example.localtrader.orders.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentBusinessOrdersBinding
import com.example.localtrader.orders.adapters.OrdersAdapter

class BusinessOrdersFragment : Fragment(), OrdersAdapter.OnItemClickListener {

    private lateinit var binding : FragmentBusinessOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_orders, container, false)

        val adapter = OrdersAdapter(this)
        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = LinearLayoutManager(context)
        binding.recycleView.setHasFixedSize(true)

        return binding.root
    }

    override fun onItemClick(position: Int) {
        return
    }


}