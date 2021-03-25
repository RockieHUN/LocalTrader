package com.example.localtrader.utils.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.localtrader.business.models.Business

class BusinessDiffUtil(
    private val oldItems : List<Business>,
    private val newItems : List<Business>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].businessId == newItems[newItemPosition].businessId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newItem = newItems[newItemPosition]
        val oldItem = oldItems[oldItemPosition]

        return when{
            oldItem.businessId != newItem.businessId -> false
            oldItem.ownerUid != newItem.ownerUid -> false
            oldItem.name != newItem.name -> false
            oldItem.category != newItem.category -> false
            oldItem.description != newItem.description -> false
            oldItem.email != newItem.email -> false
            oldItem.telephone != newItem.telephone -> false
            oldItem.longitude != newItem.longitude -> false
            oldItem.latitude != newItem.latitude -> false
            oldItem.facebook_link != newItem.facebook_link -> false
            oldItem.instagram_link != newItem.instagram_link -> false
            else -> true
        }
    }

}