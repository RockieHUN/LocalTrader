package com.example.localtrader.orders.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentOrderChatBinding
import com.example.localtrader.orders.adapters.OrderChatAdapter
import com.example.localtrader.orders.models.ChatMessage
import com.example.localtrader.viewmodels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OrderChatFragment : Fragment(),
        OrderChatAdapter.OnContentUpdateListener
{

    private val chatViewModel : ChatViewModel by activityViewModels()

    private lateinit var auth : FirebaseAuth
    private lateinit var binding : FragmentOrderChatBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
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
            sendMessage()
        }
    }

    private fun sendMessage(){

        if (auth.currentUser != null){
            val messageText = binding.messageInput.text.toString()

            if (messageText.isNotEmpty()){
                binding.messageInput.setText("")

                val uid = auth.currentUser!!.uid
                val message = ChatMessage(
                    senderId = uid,
                    message = messageText
                )

                chatViewModel.sendMessage(message)
            }
        }

    }


    private fun createRecycle(){
        if (auth.currentUser != null){
            val adapter = OrderChatAdapter(auth.currentUser!!.uid, this)
            binding.recycleView.adapter = adapter
            val verticalLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.recycleView.layoutManager = verticalLayout
            binding.recycleView.setHasFixedSize(true)

            chatViewModel.messages.observe(viewLifecycleOwner,{ messages ->
                adapter.updateData(messages)
            })

            chatViewModel.loadChat()
        }

    }

    override fun onContentUpdate(position : Int) {
        binding.recycleView.scrollToPosition(position)
    }


}