package com.example.localtrader.orders.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R
import com.example.localtrader.orders.models.ChatMessage

class OrderChatAdapter (): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ChatMessage>()

    companion object {
        const val RECEIVED = 1
        const val SENT = 2
    }


    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener{

        val messageView = itemView.findViewById<TextView>(R.id.message_text)

        fun bind(position : Int){
            val currentItem = items[position]
            messageView.text = currentItem.message
        }

        override fun onClick(v: View?) {
            return
        }
    }


    inner class RecievedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val messageView = itemView.findViewById<TextView>(R.id.message_text)

        fun bind(position: Int){
            val currentItem = items[position]
            messageView.text = currentItem.message
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            /*if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }*/
        }
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
        if (items[position].type == RECEIVED) {
            (holder as RecievedMessageViewHolder).bind(position)
        } else {
            (holder as SentMessageViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun getItemCount(): Int = items.size


    fun addItem(message : ChatMessage){
        items.add(message)
        notifyDataSetChanged()
    }
}