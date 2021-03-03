package com.example.localtrader.utils.date


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyDateTime {

    companion object{

        var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        fun getCurrentTime() : String{
            val date = LocalDateTime.now()
            return date.format(formatter)
        }

        fun stringToDateTime(string : String): LocalDateTime {
            return LocalDateTime.parse(string, formatter)
        }
    }
}