package com.example.localtrader.utils.date

import com.example.localtrader.orders.models.OrderRequest

class DateComparator {

    companion object : Comparator<OrderRequest>{
        override fun compare(a: OrderRequest, b: OrderRequest): Int {

            when{
                MyDateTime.stringToDateTime(a.date) > MyDateTime.stringToDateTime(b.date) -> return -1
                MyDateTime.stringToDateTime(a.date) < MyDateTime.stringToDateTime(b.date) -> return 1
                MyDateTime.stringToDateTime(a.date) == MyDateTime.stringToDateTime(b.date) -> return 0
            }

            return 0
        }

    }
}