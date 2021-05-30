package com.example.localtrader.business.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.product.models.Product
import com.example.localtrader.utils.diffUtils.ProductDiffUtil
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BusinessProfileAdapter (
    private val listener : OnItemClickListener,
    private val onProductAddedListener : OnProductAddedListener,
    private val activity : Activity,
    private var items : List<Product>
): RecyclerView.Adapter<BusinessProfileAdapter.DataViewHolder>() {

    private val storage = Firebase.storage


    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val productImageView = itemView.findViewById<ImageView>(R.id.product_image)
        val productNameView = itemView.findViewById<TextView>(R.id.product_name)
        val productPriceView = itemView.findViewById<TextView>(R.id.product_price)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.myOnClickListener(items[position])
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun myOnClickListener(product : Product)
    }

    interface OnProductAddedListener{
        fun scrollToPositionZero()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BusinessProfileAdapter.DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.business_profile_product_preview, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BusinessProfileAdapter.DataViewHolder, position: Int) {
        val currentItem = items[position]

        holder.productNameView.text = currentItem.name
        holder.productPriceView.text = "${currentItem.price} RON"

        storage.reference.child("products/${currentItem.productId}/PRODUCT_IMAGE_400")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(activity)
                    .load(uri)
                    .centerCrop()
                    .into(holder.productImageView)
            }
    }

    fun updateData(newItems: List<Product>){
        val diffUtil = ProductDiffUtil(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
        onProductAddedListener.scrollToPositionZero()
    }

    override fun getItemCount(): Int = items.size
}