package com.example.localtrader.product

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.product.models.Product
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProductGridRecycleAdapter (
    private  val listener : ProductGridRecycleAdapter.OnItemClickListener,
    private val activity: Activity,
    private val items : List<Product>
        ) : RecyclerView.Adapter<ProductGridRecycleAdapter.DataViewHolder>(){

    private val storage = Firebase.storage

    inner class DataViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val imageView = itemView.findViewById<ImageView>(R.id.product_image)

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
    ): ProductGridRecycleAdapter.DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.product_grid_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductGridRecycleAdapter.DataViewHolder, position: Int) {
        val currentItem = items[position]

        storage.reference.child("products/${currentItem.productId}/image")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(activity)
                    .load(uri)
                    .centerCrop()
                    .into(holder.imageView)
            }


    }

    override fun getItemCount(): Int = items.size


}