package com.example.localtrader.utils.constants

class OrderStatus {

    companion object{

        //integers
        const val WAITING_FOR_CONFIRMATION = 1
        const val ACCEPTED = 2
        const val WORKING_ON_IT = 3
        const val DONE = 4
        const val DECLINED = 5

        //colors
        const val COLOR_WAITING_FOR_CONFIRMATION = "#b302db"
        const val COLOR_ACCEPTED = "#0fba3d"
        const val COLOR_WORKING_ON_IT = "#f1bb64"
        const val COLOR_DONE = "#55bbe0"
        const val COLOR_DECLINED = "#ba163d"
    }
}