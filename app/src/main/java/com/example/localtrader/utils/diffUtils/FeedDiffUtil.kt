package com.example.localtrader.utils.diffUtils

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.localtrader.feed.models.*

class FeedDiffUtil(
    private val oldItems : List<FeedItem>,
    private val newItems : List<FeedItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        //Log.d("MYFEED","${oldItem::class} == ${newItem::class}")
        if (oldItem::class.simpleName != newItem::class.simpleName) return false

        if (newItem is FeedBusinessItem) {
            oldItem as FeedBusinessItem

            return oldItem.businessId == newItem.businessId
        }

        if (newItem is FeedAdItem){
            oldItem as FeedAdItem
            return oldItem.id == newItem.id
        }

        if (newItem is FeedLoadItem){
            oldItem as FeedLoadItem
            return oldItem.id == newItem.id
        }

        if (newItem is FeedNoMoreItem){
            oldItem as FeedNoMoreItem
            return oldItem.id == newItem.id
        }

        return true
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        if (oldItem::class != newItem::class) return false

        if (newItem is FeedBusinessItem){
            oldItem as FeedBusinessItem
            return areBusinessContentsTheSame(oldItem, newItem)
        }

        if (newItem is FeedAdItem){
            oldItem as FeedAdItem
            return oldItem.id == newItem.id
        }

        if (newItem is FeedLoadItem){
            oldItem as FeedLoadItem
            return oldItem.id == newItem.id
        }

        if (newItem is FeedNoMoreItem){
            oldItem as FeedNoMoreItem
            return oldItem.id == newItem.id
        }

        return true
    }

    private fun areBusinessContentsTheSame(oldItem : FeedBusinessItem, newItem : FeedBusinessItem) : Boolean{
        return when{
            oldItem.businessId != newItem.businessId -> false
            oldItem.ownerUid != newItem.ownerUid -> false
            oldItem.name != newItem.name -> false
            oldItem.category != newItem.category -> false
            oldItem.description != newItem.description -> false
            oldItem.email != newItem.email -> false
            oldItem.telephone != newItem.telephone -> false
            else -> true
        }
    }
}