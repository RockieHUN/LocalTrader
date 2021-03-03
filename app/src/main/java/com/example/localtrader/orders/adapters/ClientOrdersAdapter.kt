package com.example.localtrader.orders.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R
import com.example.localtrader.orders.models.OrderRequest
import org.w3c.dom.Text

class ClientOrdersAdapter (
    private val listener : OnItemClickListener,
    private val items : List<OrderRequest>,
    private val context : Context
): RecyclerView.Adapter<ClientOrdersAdapter.DataViewHolder>() {

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val productNameView = itemView.findViewById<TextView>(R.id.product_name)
        val businessNameView = itemView.findViewById<TextView>(R.id.business_name)
        val priceView = itemView.findViewById<TextView>(R.id.product_price)
        val dateView = itemView.findViewById<TextView>(R.id.date)
        val statusView = itemView.findViewById<TextView>(R.id.status)
        val productCountView = itemView.findViewById<TextView>(R.id.count)

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
    ): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.client_order_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = items[position]

        holder.productNameView.text = currentItem.productName
        //holder.businessNameView.text = currentItem.bu
        holder.priceView.text = currentItem.sum.toString()
        holder.dateView.text = currentItem.date
        holder.productCountView.text = currentItem.count.toString()

        when(currentItem.status){
            1 -> holder.statusView.text = context.resources.getString(R.string.order_status_not_confirmed)
        }
    }

    override fun getItemCount(): Int = items.size
}