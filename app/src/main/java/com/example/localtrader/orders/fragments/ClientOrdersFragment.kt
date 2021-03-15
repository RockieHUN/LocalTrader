package com.example.localtrader.orders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentClientOrdersBinding
import com.example.localtrader.orders.adapters.ClientOrdersAdapter
import com.example.localtrader.repositories.OrdersRepository
import com.example.localtrader.utils.date.DateComparator

class ClientOrdersFragment : Fragment(), ClientOrdersAdapter.OnItemClickListener {

    private lateinit var binding : FragmentClientOrdersBinding
    private lateinit var ordersRepository: OrdersRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ordersRepository = OrdersRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_orders, container, false)

        createRecycle()

        return binding.root
    }

    private fun createRecycle(){

        val adapter = ClientOrdersAdapter(this, listOf(), requireContext())
        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = LinearLayoutManager(context)
        binding.recycleView.setHasFixedSize(true)


        ordersRepository.clientOrders.observe(viewLifecycleOwner,{ orders ->
            if (orders.isNotEmpty()) binding.noOrdersYet.visibility = View.GONE
            val sortedList = orders.sortedWith(DateComparator)
            adapter.updateData(sortedList)
        })
        ordersRepository.loadClientOrders()
    }



    override fun onItemClick(position: Int) {
        return
    }






}