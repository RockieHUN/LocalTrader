package com.example.localtrader.chat.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.chat.models.MessageInfo
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MessagesAdapter(
    private val activity : Activity,
    private val listener : OnItemClickListener
) : RecyclerView.Adapter<MessagesAdapter.DataViewHolder>(){

    private var items: List<MessageInfo> = listOf()
    private val storage = Firebase.storage

    inner class DataViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val nameView = itemView.findViewById<TextView>(R.id.name)
        val lastMessageView = itemView.findViewById<TextView>(R.id.last_message)
        val imageView = itemView.findViewById<ImageView>(R.id.image)
        val item = itemView.findViewById<CardView>(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesAdapter.DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return DataViewHolder(itemView)
    }

    interface OnItemClickListener{
        fun onItemClick(messageInfo : MessageInfo)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = items[position]

        holder.nameView.text = currentItem.senderName
        holder.lastMessageView.text = currentItem.last_message

        holder.item.setOnClickListener {
            listener.onItemClick(currentItem)
        }

        loadImage(currentItem, holder.imageView)
    }

    private fun loadImage(item : MessageInfo, imageView : ImageView){
        //if type is "user"
        if (item.senderType == 1){
            storage.reference.child("users/${item.senderId}/profilePicture").downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(activity)
                        .load(uri)
                        .centerCrop()
                        .into(imageView)
                }
        }

        //if type is "business"
        else{
            storage.reference.child("businesses/${item.senderId}/logo").downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(activity)
                        .load(uri)
                        .centerCrop()
                        .into(imageView)
                }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(messages : List<MessageInfo>){
        items = messages
        notifyDataSetChanged()
    }

}