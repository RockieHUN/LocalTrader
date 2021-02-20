package com.example.localtrader.main_screens.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.business.models.Business
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PopularBusinessesAdapter(
    private  val listener : OnItemClickListener,
    private var items : MutableList<Business>,
    private val activity : Activity
): RecyclerView.Adapter<PopularBusinessesAdapter.DataViewHolder>() {

    private val storage = Firebase.storage

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val businessNameView = itemView.findViewById<TextView>(R.id.business_name)
        val businessCategoryView = itemView.findViewById<TextView>(R.id.business_category)
        val businessLogoView = itemView.findViewById<ImageView>(R.id.business_logo)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopularBusinessesAdapter.DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.business_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PopularBusinessesAdapter.DataViewHolder, position: Int) {
        val currentItem = items[position]

        Log.d("****", items.size.toString())
        Log.d("****", position.toString())
        Log.d("****", currentItem.name)
        Log.d("****", currentItem.category)
        Log.d("****", currentItem.businessId)
        Log.d("****", currentItem.email)


        holder.businessNameView.text = currentItem.name
        holder.businessCategoryView.text = currentItem.category

        storage.reference.child("businesses/${currentItem.businessId}/logo")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(activity)
                    .load(uri)
                    .centerCrop()
                    .into(holder.businessLogoView)
            }
    }

    override fun getItemCount(): Int = items.size
}