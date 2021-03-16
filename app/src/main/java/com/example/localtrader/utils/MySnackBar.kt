package com.example.localtrader.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class MySnackBar {

    companion object{

        fun createSnackBar(view : View, message : String){

            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            snackbar.show()
        }


        }
}