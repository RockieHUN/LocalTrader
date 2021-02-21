package com.example.localtrader.main_screens.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.product.models.Product
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text

class PopularProductsAdapter (
    private  val listener : OnItemClickListener,
    private val activity : Activity,
    private var items : MutableList<Product>
        ): RecyclerView.Adapter<PopularProductsAdapter.DataViewHolder>() {

    private val storage = Firebase.storage



    inner class DataViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView), View.OnClickListener{

        val productImageView = itemView.findViewById<ImageView>(R.id.product_image)
        val productNameView = itemView.findViewById<TextView>(R.id.product_name)
        val productPriceView = itemView.findViewById<TextView>(R.id.product_price)

        init{
            itemView.setOnClickListener(this)
        }

       override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }

    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularProductsAdapter.DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item,parent,false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PopularProductsAdapter.DataViewHolder, position: Int) {
        val currentItem = items[position]

        holder.productNameView.text = currentItem.name
        holder.productPriceView.text = currentItem.price.toString()

        storage.reference.child("products/${currentItem.productId}/image")
            .downloadUrl.addOnSuccessListener { uri ->
                Glide.with(activity)
                    .load(uri)
                    .centerCrop()
                    .into(holder.productImageView)
            }
    }

    override fun getItemCount(): Int = items.size
}