package com.example.localtrader.utils.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.localtrader.product.models.Product

class MyDiffUtil(
    private val oldList : List<Product>,
    private val newList : List<Product>
) : DiffUtil.Callback(){

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].productId == newList[newItemPosition].productId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when{
            oldItem.productId != newItem.productId -> false
            oldItem.ownerId != newItem.ownerId -> false
            oldItem.businessId != newItem.businessId -> false
            oldItem.name != newItem.name -> false
            oldItem.price != newItem.price -> false
            oldItem.description != newItem.description -> false
            oldItem.businessName != newItem.businessName -> false
            else -> true
        }
    }

}