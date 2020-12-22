package com.example.localtrader.main_screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R

class RecommendedProductsAdapter (
    private  val listener : OnItemClickListener
        ): RecyclerView.Adapter<RecommendedProductsAdapter.DataViewHolder>() {

    private val items = mutableListOf<Int>(1,2,3,4,5,6)


    inner class DataViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView), View.OnClickListener{



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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedProductsAdapter.DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item,parent,false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecommendedProductsAdapter.DataViewHolder, position: Int) {
        val currentItem = items[position]
    }

    override fun getItemCount(): Int = items.size
}