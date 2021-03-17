package com.example.localtrader.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class MySnackBar {

    companion object{

        fun createSnackBar(view : View, message : String){

            val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackBar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            snackBar.show()
        }


        }
}