package com.example.localtrader

import com.example.localtrader.utils.date.MyDateTime

abstract class BaseClass(
    var date : String = MyDateTime.getCurrentTime()
)