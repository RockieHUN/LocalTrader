package com.example.localtrader.orders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentBusinessOrdersBinding
import com.example.localtrader.orders.adapters.BusinessOrdersAdapter
import com.example.localtrader.repositories.OrdersRepository
import com.example.localtrader.utils.comparators.OrderComparator
import com.example.localtrader.viewmodels.UserViewModel

class BusinessOrdersFragment : Fragment(), BusinessOrdersAdapter.OnItemClickListener {

    private lateinit var binding : FragmentBusinessOrdersBinding
    private lateinit var ordersRepository: OrdersRepository

    private val userViewModel : UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ordersRepository = OrdersRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_orders, container, false)
        createRecycle()
        return binding.root
    }

    private fun createRecycle(){

        val adapter = BusinessOrdersAdapter(this, listOf(), requireContext())
        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = LinearLayoutManager(context)
        binding.recycleView.setHasFixedSize(true)

        ordersRepository.businessOrders.observe(viewLifecycleOwner,{ orders ->

            if (orders.isNotEmpty()){
                binding.noOrdersYet.visibility = View.GONE
            }
            val sortedList = orders.sortedWith(OrderComparator)
            adapter.updateData(sortedList)
        })
        ordersRepository.loadBusinessOrders(userViewModel.user.value!!.businessId)
    }

    override fun onItemClick(position: Int) {
        return
    }


}