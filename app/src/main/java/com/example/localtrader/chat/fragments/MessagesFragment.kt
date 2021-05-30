package com.example.localtrader.chat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localtrader.R
import com.example.localtrader.chat.adapters.MessagesAdapter
import com.example.localtrader.chat.models.ChatLoadInformation
import com.example.localtrader.chat.models.MessageInfo
import com.example.localtrader.databinding.FragmentMessagesBinding
import com.example.localtrader.viewmodels.MessagesViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MessagesFragment : Fragment(), MessagesAdapter.OnItemClickListener {

    private lateinit var binding : FragmentMessagesBinding
    private val messagesViewModel : MessagesViewModel by activityViewModels()
    private val userViewModel : UserViewModel by activityViewModels()
    private val auth  = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false)
        createRecycle()
        return binding.root
    }

    private fun createRecycle(){
        val adapter = MessagesAdapter(requireActivity(),this)
        binding.recycleView.adapter = adapter
        val verticalLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycleView.layoutManager = verticalLayout
        binding.recycleView.setHasFixedSize(true)

        messagesViewModel.messagesInfoList.observe(viewLifecycleOwner, { messages->
            if (messages.isEmpty()) binding.noItemsHolder.visibility = View.VISIBLE
            adapter.updateData(messages)
        })
        if (auth.currentUser != null)
            messagesViewModel.loadMessages(auth.currentUser!!.uid, userViewModel.user.value?.businessId)

    }

    override fun onItemClick(messageInfo: MessageInfo) {

        if (messageInfo.senderType == 1){
            messagesViewModel.chatLoadInformation = ChatLoadInformation(
                businessId = userViewModel.user.value!!.businessId,
                userId = messageInfo.senderId,
                whoIsTheOther = 1
            )
        }
        else{
            messagesViewModel.chatLoadInformation = ChatLoadInformation(
                businessId = messageInfo.senderId,
                userId = auth.currentUser!!.uid,
                whoIsTheOther = 2
            )
        }

        findNavController().navigate(R.id.action_messagesFragment_to_chatFragment)
    }

}