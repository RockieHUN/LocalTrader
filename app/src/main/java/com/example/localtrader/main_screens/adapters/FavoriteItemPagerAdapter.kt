package com.example.localtrader.main_screens.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.product.models.Product
import com.example.localtrader.utils.diffUtils.ProductDiffUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FavoriteItemPagerAdapter(
    private var items: List<Product>,
    private val activity: Activity,
    private val listener: MyOnClickListener
) : RecyclerView.Adapter<FavoriteItemPagerAdapter.DataViewHolder>() {

    private val storage = Firebase.storage
    private val auth = Firebase.auth


    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productImageView = itemView.findViewById<ImageView>(R.id.product_image)
        val favoritesButton = itemView.findViewById<ImageButton>(R.id.favorite_button)
        val priceView = itemView.findViewById<TextView>(R.id.price)
        val descriptionView = itemView.findViewById<TextView>(R.id.product_description)
        val businessNameView = itemView.findViewById<TextView>(R.id.business_name)
        val productNameView = itemView.findViewById<TextView>(R.id.product_name)
        val orderbuttonHolder = itemView.findViewById<CardView>(R.id.orderButtonHolder)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteItemPagerAdapter.DataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_favorite_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteItemPagerAdapter.DataViewHolder, position: Int) {
        val currentItem = items[position]

        holder.priceView.text = currentItem.price.toString()
        holder.descriptionView.text = currentItem.description
        holder.businessNameView.text = currentItem.businessName
        holder.productNameView.text = currentItem.name


        if (auth.currentUser != null && auth.currentUser!!.uid != currentItem.ownerId){
            holder.orderbuttonHolder.setOnClickListener{
                listener.onOrderButtonClick(currentItem)
            }
        }
        else{
            holder.orderbuttonHolder.visibility = View.GONE
        }


        holder.favoritesButton.setOnClickListener {
            listener.onFavoriteButtonClick(currentItem)
        }

        storage.reference.child("products/${currentItem.productId}/image")
            .downloadUrl.addOnSuccessListener { uri ->
                Glide.with(activity)
                    .load(uri)
                    .centerCrop()
                    .into(holder.productImageView)
            }
    }

    interface MyOnClickListener {
        fun onFavoriteButtonClick(product : Product)
        fun onOrderButtonClick(product : Product)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Product>) {
        val diffUtil = ProductDiffUtil(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
}