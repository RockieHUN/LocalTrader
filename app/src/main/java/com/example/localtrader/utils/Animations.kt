package com.example.localtrader.utils

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.localtrader.R
import kotlinx.coroutines.delay

class Animations {

    companion object{

        suspend fun animateError(layout : ConstraintLayout, errorMessage : String)
        {
            val textView = layout.findViewById<TextView>(R.id.error_message)
            textView.text = errorMessage

            layout.animate()
                .translationYBy(200f)
                .duration = 400L

            delay(4000)
            layout.animate()
                .translationYBy(-200f)
                .duration = 400L

        }
    }
}