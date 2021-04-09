package com.example.localtrader.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R
import com.example.localtrader.chat.models.ChatMessage
import com.example.localtrader.utils.diffUtils.ChatMessageDiffUtil

class ChatAdapter (
    private val userId : String,
    private val listener : OnContentUpdateListener
        ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<ChatMessage>()

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val messageView = itemView.findViewById<TextView>(R.id.message_text)

        fun bind(position : Int){
            val currentItem = items[position]
            messageView.text = currentItem.message
        }

    }


    inner class RecievedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val messageView = itemView.findViewById<TextView>(R.id.message_text)

        fun bind(position: Int){
            val currentItem = items[position]
            messageView.text = currentItem.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (items[position].senderId == userId) return 2
        else return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 2){
            SentMessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_sent_item, parent, false)
            )
        } else RecievedMessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.chat_recieved_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items[position].senderId != userId) {
            (holder as RecievedMessageViewHolder).bind(position)
        } else {
            (holder as SentMessageViewHolder).bind(position)
        }
    }


    override fun getItemCount(): Int = items.size


    fun updateData(messages : List<ChatMessage>){
        val diffUtil = ChatMessageDiffUtil(items, messages)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        items = messages
        diffResult.dispatchUpdatesTo(this)
        listener.onContentUpdate(items.size - 1)
    }

    interface OnContentUpdateListener{
        fun onContentUpdate(position : Int)
    }


}