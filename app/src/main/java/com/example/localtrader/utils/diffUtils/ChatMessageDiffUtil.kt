package com.example.localtrader.utils.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.localtrader.chat.models.ChatMessage

class ChatMessageDiffUtil(
    private val oldItems : List<ChatMessage>,
    private val newItems : List<ChatMessage>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int  = oldItems.size


    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newItem = newItems[newItemPosition]
        val oldItem = oldItems[oldItemPosition]
        return when{
            newItem.date != oldItem.date -> false
            newItem.message != oldItem.message -> false
            newItem.senderId != oldItem.senderId -> false
            else -> true
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newItem = newItems[newItemPosition]
        val oldItem = oldItems[oldItemPosition]
        return when{
            newItem.date != oldItem.date -> false
            newItem.message != oldItem.message -> false
            newItem.senderId != oldItem.senderId -> false
            else -> true
        }
    }
}