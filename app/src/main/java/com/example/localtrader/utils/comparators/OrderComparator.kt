package com.example.localtrader.utils.comparators

import com.example.localtrader.orders.models.OrderRequest
import com.example.localtrader.utils.date.MyDateTime

class OrderComparator {

    companion object : Comparator<OrderRequest> {
        override fun compare(a: OrderRequest, b: OrderRequest): Int {

            if (a.status < b.status) return -1
            if (a.status > b.status) return 1
            if (a.status == b.status){
                return if (MyDateTime.stringToDateTime(a.date) < MyDateTime.stringToDateTime(b.date)) -1
                else 1

            }

            return 0
        }
    }
}