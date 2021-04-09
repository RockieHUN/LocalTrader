package com.example.localtrader.feed.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.business.models.Business
import com.example.localtrader.feed.models.FeedBusinessItem
import com.example.localtrader.feed.models.FeedItem
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FeedAdapter(
    private val activity : Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<FeedItem>()
    private var storage = Firebase.storage

    //ViewHolders
    inner class BusinessViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val logoView = itemView.findViewById<ImageView>(R.id.business_logo)
        val businessNameView = itemView.findViewById<TextView>(R.id.business_name)
        val businessDescriptionView = itemView.findViewById<TextView>(R.id.business_description)

        fun bind(position: Int){
            val currentItem = items[position] as FeedBusinessItem

            businessDescriptionView.text = currentItem.description
            businessNameView.text = currentItem.name

            storage.reference.child("businesses/${currentItem.businessId}/logo")
                .downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(activity)
                        .load(uri)
                        .centerCrop()
                        .into(logoView)
                }
        }
    }

    inner class ProductViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bind(position: Int){
            val currentItem = items[position]
        }
    }

    inner class AdViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bind(position: Int){
            val currentItem = items[position]
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is FeedBusinessItem -> 1
            else -> 0
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            1 -> BusinessViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.feed_business_item, parent, false)
            )
            else -> BusinessViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.feed_business_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items[position] is FeedBusinessItem){
            (holder as BusinessViewHolder).bind(position)
        }
    }

    fun updateData(newItems : List<FeedItem>){
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}