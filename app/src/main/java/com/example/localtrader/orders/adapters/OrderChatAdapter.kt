package com.example.localtrader.orders.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R

class OrderChatAdapter (): RecyclerView.Adapter<OrderChatAdapter.DataViewHolder>() {

    private val items = mutableListOf<String>()

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val messageView = itemView.findViewById<TextView>(R.id.message_text)

        override fun onClick(v: View?) {
            val position = adapterPosition
            /*if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }*/
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_sent_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = items[position]
        holder.messageView.text = currentItem
    }

    override fun getItemCount(): Int = items.size


    fun addItem(message : String){
        items.add(message)
        notifyDataSetChanged()
    }
}