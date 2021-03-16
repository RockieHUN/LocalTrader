package com.example.localtrader.utils.comparators

import com.example.localtrader.BaseClass
import com.example.localtrader.utils.date.MyDateTime

class DateComparator {

   companion object : Comparator<BaseClass> {
       override fun compare(a: BaseClass, b: BaseClass): Int {

           when {
               MyDateTime.stringToDateTime(a.date) > MyDateTime.stringToDateTime(b.date) -> return -1
               MyDateTime.stringToDateTime(a.date) < MyDateTime.stringToDateTime(b.date) -> return 1
               MyDateTime.stringToDateTime(a.date) == MyDateTime.stringToDateTime(b.date) -> return 0
           }

           return 0
       }
   }

}