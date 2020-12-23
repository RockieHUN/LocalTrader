package com.example.localtrader.business.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R
import com.example.localtrader.main_screens.adapters.PopularBusinessesAdapter

class BusinessOrdersAdapter (
    private  val listener : OnItemClickListener
): RecyclerView.Adapter<BusinessOrdersAdapter.DataViewHolder>() {

    private val items = mutableListOf<Int>(1, 2, 3, 4, 5, 6)

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

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
    ): BusinessOrdersAdapter.DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.business_order_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BusinessOrdersAdapter.DataViewHolder, position: Int) {
        val currentItem = items[position]
    }

    override fun getItemCount(): Int = items.size
}