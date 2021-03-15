package com.example.localtrader.orders.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R
import com.example.localtrader.orders.models.OrderRequest
import com.example.localtrader.utils.constants.OrderStatus
import com.example.localtrader.utils.diffUtils.OrderRequestDiffUtil

class BusinessOrdersAdapter (
    private val listener : OnItemClickListener,
    private var items : List<OrderRequest>,
    private val context : Context
): RecyclerView.Adapter<BusinessOrdersAdapter.DataViewHolder>() {

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val productNameView = itemView.findViewById<TextView>(R.id.product_name)
        val ordererView = itemView.findViewById<TextView>(R.id.orderer)
        val businessNameView = itemView.findViewById<TextView>(R.id.business_name)
        val priceView = itemView.findViewById<TextView>(R.id.product_price)
        val dateView = itemView.findViewById<TextView>(R.id.date)
        val statusView = itemView.findViewById<TextView>(R.id.status)
        val productCountView = itemView.findViewById<TextView>(R.id.count)
        val acceptLayerView = itemView.findViewById<ConstraintLayout>(R.id.accept_layer)
        val counterImageView = itemView.findViewById<ImageView>(R.id.imageView)

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
            LayoutInflater.from(parent.context).inflate(R.layout.business_order_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = items[position]

        holder.productNameView.text = currentItem.productName
        //holder.businessNameView.text = currentItem.bu
        holder.priceView.text = currentItem.sum.toString()
        holder.dateView.text = currentItem.date
        holder.productCountView.text = currentItem.count.toString()
        holder.ordererView.text = "${currentItem.clientFirstName} ${currentItem.clientLastName}"

        if (currentItem.status == OrderStatus.WAITING_FOR_CONFIRMATION){
            holder.acceptLayerView.visibility = View.VISIBLE
        }

        when(currentItem.status){
            OrderStatus.WAITING_FOR_CONFIRMATION ->{
                holder.statusView.text = context.resources.getString(R.string.order_status_not_confirmed)
                holder.statusView.visibility = View.GONE
                holder.counterImageView.visibility = View.GONE
                holder.productCountView.visibility = View.GONE
            }

        }
    }

    fun updateData(newItems: List<OrderRequest>){
        val diffUtil = OrderRequestDiffUtil(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = items.size

}