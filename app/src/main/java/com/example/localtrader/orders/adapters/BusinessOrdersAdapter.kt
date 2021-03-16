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

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val item = itemView.findViewById<ConstraintLayout>(R.id.item)
        val productNameView = itemView.findViewById<TextView>(R.id.product_name)
        val ordererView = itemView.findViewById<TextView>(R.id.orderer)
        val businessNameView = itemView.findViewById<TextView>(R.id.business_name)
        val priceView = itemView.findViewById<TextView>(R.id.product_price)
        val dateView = itemView.findViewById<TextView>(R.id.date)
        val statusView = itemView.findViewById<TextView>(R.id.status)
        val productCountView = itemView.findViewById<TextView>(R.id.count)
        val acceptLayerView = itemView.findViewById<ConstraintLayout>(R.id.accept_layer)
        val counterImageView = itemView.findViewById<ImageView>(R.id.imageView)
        val acceptButton = itemView.findViewById<ImageButton>(R.id.accept_button)
        val declineButton = itemView.findViewById<ImageButton>(R.id.decline_button)

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


        //do stuff based on status
        when(currentItem.status){
            OrderStatus.WAITING_FOR_CONFIRMATION ->{
                holder.statusView.text = context.resources.getString(R.string.order_status_not_confirmed)
                holder.statusView.visibility = View.GONE
                holder.counterImageView.visibility = View.GONE
                holder.productCountView.visibility = View.GONE
                holder.acceptLayerView.visibility = View.VISIBLE

                //if order ACCEPTED
                holder.acceptButton.setOnClickListener {
                    firestore.collection("orderRequests")
                        .document(currentItem.orderRequestId)
                        .update("status", OrderStatus.ACCEPTED)
                        .addOnSuccessListener {
                            holder.acceptLayerView.visibility = View.GONE
                            holder.statusView.visibility = View.VISIBLE
                            holder.counterImageView.visibility = View.VISIBLE
                            holder.productCountView.visibility = View.VISIBLE
                            holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_ACCEPTED))

                            //update adapter items
                            var newList = items
                            newList[position].status = OrderStatus.ACCEPTED
                            newList = newList.sortedWith(OrderComparator)
                            updateData(newList)
                        }
                }

                //if order declined
                holder.declineButton.setOnClickListener {
                    firestore.collection("orderRequests")
                        .document(currentItem.orderRequestId)
                        .update("status", OrderStatus.DECLINED)
                        .addOnSuccessListener {
                            holder.acceptLayerView.visibility = View.GONE
                            holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_DECLINED))
                            holder.statusView.visibility = View.VISIBLE
                            holder.counterImageView.visibility = View.VISIBLE
                            holder.productCountView.visibility = View.VISIBLE

                            //update adapter items
                            var newList = items
                            newList[position].status = OrderStatus.DECLINED
                            newList = newList.sortedWith(OrderComparator)
                            updateData(newList)
                        }
                }
            }

            OrderStatus.ACCEPTED ->{
                holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_ACCEPTED))
                holder.statusView.text = context.resources.getString(R.string.order_status_accepted)
                //TODO: ITEM ONCLICK
            }

            OrderStatus.DECLINED -> {
                holder.item.setBackgroundColor(Color.parseColor(OrderStatus.COLOR_DECLINED))
                holder.statusView.text = context.resources.getString(R.string.order_status_declined)
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