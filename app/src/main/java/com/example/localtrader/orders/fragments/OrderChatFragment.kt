package com.example.localtrader.orders.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentOrderChatBinding
import com.example.localtrader.main_screens.adapters.PopularProductsAdapter
import com.example.localtrader.orders.adapters.OrderChatAdapter

class OrderChatFragment : Fragment() {

    private lateinit var binding : FragmentOrderChatBinding
    private lateinit var adapter : OrderChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_chat, container, false)
        setUpVisuals()
        createRecycle()
        setUpListeners()
        return binding.root
    }

    private fun setUpVisuals(){
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    private fun setUpListeners(){
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotEmpty()){
                binding.messageInput.setText("")
                adapter.addItem(message)
                binding.recycleView.scrollToPosition(adapter.itemCount-1)
            }

        }
    }


    private fun createRecycle(){
        adapter = OrderChatAdapter()
        binding.recycleView.adapter = adapter
        val verticalLayout = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recycleView.layoutManager = verticalLayout
        binding.recycleView.setHasFixedSize(true)
    }


}