package com.example.localtrader.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R

class MessagesAdapter(
    private val items: List<String> = listOf()
) : RecyclerView.Adapter<MessagesAdapter.DataViewHolder>(){

    inner class DataViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val nameView = itemView.findViewById<TextView>(R.id.name)
        val lastMessageView = itemView.findViewById<TextView>(R.id.last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesAdapter.DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = items.size

}