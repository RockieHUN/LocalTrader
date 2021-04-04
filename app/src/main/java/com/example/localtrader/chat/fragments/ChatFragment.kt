package com.example.localtrader.chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.chat.adapters.ChatAdapter
import com.example.localtrader.chat.models.ChatMessage
import com.example.localtrader.databinding.FragmentChatBinding

import com.example.localtrader.viewmodels.MessagesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment(),
        ChatAdapter.OnContentUpdateListener
{

    private val messagesViewModel : MessagesViewModel by activityViewModels()

    private lateinit var auth : FirebaseAuth
    private lateinit var binding : FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
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

                messagesViewModel.sendMessage(message)
            }
        }
    }


    private fun createRecycle(){
        if (auth.currentUser != null){
            val adapter = ChatAdapter(auth.currentUser!!.uid, this)
            binding.recycleView.adapter = adapter
            val verticalLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.recycleView.layoutManager = verticalLayout
            binding.recycleView.setHasFixedSize(true)

            messagesViewModel.chatItemMessages.observe(viewLifecycleOwner,{ messages ->
                adapter.updateData(messages)
            })

            messagesViewModel.loadChat(binding.header)
        }

    }

    override fun onContentUpdate(position : Int) {
        binding.recycleView.scrollToPosition(position)
    }


}