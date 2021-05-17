package com.example.localtrader.search.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R
import com.example.localtrader.search.models.SearchResult

class SearchAdapter(
    private val listener : onItemClickListener
) : RecyclerView.Adapter<SearchAdapter.DataViewHolder>() {

    private var items : List<SearchResult> = listOf()

    inner class DataViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textView = itemView.findViewById<TextView>(R.id.result)
        val item = itemView.findViewById<CardView>(R.id.item)

    }

    interface onItemClickListener{
        fun onItemClicked(id : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = items[position]

        holder.textView.text = currentItem.businessName
        holder.item.setOnClickListener{
            listener.onItemClicked(currentItem.businessId)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems : List<SearchResult>){
        items = newItems
        notifyDataSetChanged()
    }
}