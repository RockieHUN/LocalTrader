package com.example.localtrader.utils.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.localtrader.feed.models.FeedAdItem
import com.example.localtrader.feed.models.FeedBusinessItem
import com.example.localtrader.feed.models.FeedItem

class FeedDiffUtil(
    private val oldItems : List<FeedItem>,
    private val newItems : List<FeedItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        if (oldItem::class.simpleName != newItem::class.simpleName) return false
        else{
            if (newItem is FeedBusinessItem){
                oldItem as FeedBusinessItem

                if (oldItem.businessId != newItem.businessId) return false
            }
            if (newItem is FeedAdItem){
                oldItem as FeedAdItem
                if (oldItem.id != newItem.id) return false
            }

        }

        return true
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        if (oldItem::class.simpleName != newItem::class.simpleName) return false
        else{
            if (newItem is FeedBusinessItem){
                oldItem as FeedBusinessItem

                if (!areBusinessContentsTheSame(oldItem, newItem)) return false
            }
            if (newItem is FeedAdItem){
                oldItem as FeedAdItem
                if (oldItem.id != newItem.id) return false
            }

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