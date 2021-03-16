package com.example.localtrader.utils.comparators

import com.example.localtrader.orders.models.OrderRequest
import com.example.localtrader.utils.date.MyDateTime

class OrderComparator {

    companion object : Comparator<OrderRequest> {
        override fun compare(a: OrderRequest, b: OrderRequest): Int {

            return when {

                (a.status < b.status) && (MyDateTime.stringToDateTime(a.date) > MyDateTime.stringToDateTime(b.date)) -> -1
                (a.status < b.status) && (MyDateTime.stringToDateTime(a.date) < MyDateTime.stringToDateTime(b.date)) -> 1

                (a.status == b.status) && (MyDateTime.stringToDateTime(a.date) < MyDateTime.stringToDateTime(b.date)) -> 1
                (a.status == b.status) && (MyDateTime.stringToDateTime(a.date) > MyDateTime.stringToDateTime(b.date)) -> -1

                (a.status > b.status) -> 1
                else -> 0
            }
        }
    }
}