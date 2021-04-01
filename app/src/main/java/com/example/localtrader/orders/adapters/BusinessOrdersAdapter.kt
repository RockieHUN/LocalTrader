package com.example.localtrader.orders.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.localtrader.R
import com.example.localtrader.orders.models.OrderRequest
import com.example.localtrader.utils.comparators.OrderComparator
import com.example.localtrader.utils.constants.OrderStatus
import com.example.localtrader.utils.diffUtils.OrderRequestDiffUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BusinessOrdersAdapter (
    private val listener : OnItemClickListener,
    private var items : List<OrderRequest>,
    private val context : Context
): RecyclerView.Adapter<BusinessOrdersAdapter.DataViewHolder>() {

    private val firestore = Firebase.firestore

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val item = itemView.findViewById<ConstraintLayout>(R.id.item)
        val productNameView = itemView.findViewById<TextView>(R.id.product_name)
        val ordererView = itemView.findViewById<TextView>(R.id.orderer)
        val priceView = itemView.findViewById<TextView>(R.id.product_price)
        val dateView = itemView.findViewById<TextView>(R.id.date)
        val statusView = itemView.findViewById<TextView>(R.id.status)
        val productCountView = itemView.findViewById<TextView>(R.id.count)

    }

    interface OnItemClickListener {
        fun onItemClick(order: OrderRequest)
        fun onItemLongClick(order : OrderRequest)
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
        holder.priceView.text = currentItem.sum.toString()
        holder.dateView.text = currentItem.date
        holder.productCountView.text = currentItem.count.toString()
        holder.ordererView.text = "${currentItem.clientFirstName} ${currentItem.clientLastName}"

        if (currentItem.status == OrderStatus.WAITING_FOR_CONFIRMATION){
            holder.item.setOnClickListener {
                listener.onItemClick(currentItem)
            }
        }

        if (currentItem.status != OrderStatus.WAITING_FOR_CONFIRMATION && currentItem.status != OrderStatus.DECLINED){
            holder.item.setOnLongClickListener {
                listener.onItemLongClick(currentItem)
                true
            }
        }


        //do stuff based on status
        when(currentItem.status){
            OrderStatus.WAITING_FOR_CONFIRMATION ->{
                holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_WAITING_FOR_CONFIRMATION))
                holder.statusView.text = context.resources.getString(R.string.order_status_not_confirmed)
            }
            OrderStatus.ACCEPTED ->{
                holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_ACCEPTED))
                holder.statusView.text = context.resources.getString(R.string.order_status_accepted)
            }

            OrderStatus.DECLINED -> {
                holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_DECLINED))
                holder.statusView.text = context.resources.getString(R.string.order_status_declined)
            }

            OrderStatus.WORKING_ON_IT ->{
                holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_WORKING_ON_IT))
                holder.statusView.text = context.resources.getString(R.string.order_status_working_on_it)
            }

            OrderStatus.DONE ->{
                holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_DONE))
                holder.statusView.text = context.resources.getString(R.string.order_status_done)
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