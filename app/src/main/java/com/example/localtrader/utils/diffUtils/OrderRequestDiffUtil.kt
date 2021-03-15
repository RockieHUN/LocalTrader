package com.example.localtrader.utils.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.localtrader.orders.models.OrderRequest

class OrderRequestDiffUtil(
    private val oldItems : List<OrderRequest>,
    private val newItems : List<OrderRequest>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size
    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].orderRequestId == newItems[newItemPosition].orderRequestId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return when {
            oldItem.orderRequestId != newItem.orderRequestId -> false
            oldItem.clientId != newItem.clientId -> false
            oldItem.productId != newItem.productId -> false
            oldItem.sum != newItem.sum -> false
            oldItem.count != newItem.count -> false
            oldItem.status != newItem.status -> false
            else -> true
        }
    }
}