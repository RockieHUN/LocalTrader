package com.example.localtrader.utils.comparators

import com.example.localtrader.BaseClass
import com.example.localtrader.chat.models.MessageInfo
import com.example.localtrader.utils.date.MyDateTime

class FirebaseDateComparator {
    companion object : Comparator <MessageInfo>{
        override fun compare(a: MessageInfo, b: MessageInfo): Int {

            when {
                a.last_message_date > b.last_message_date -> return -1
                a.last_message_date < b.last_message_date -> return 1
                a.last_message_date == b.last_message_date -> return 0
            }

            return 0
        }

    }
}
