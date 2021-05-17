package com.example.localtrader.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

class MySwipeRefresh(context : Context, attrs : AttributeSet?) : SwipeRefreshLayout(context, attrs) {

    private var prevX : Float = 0.0f
    private var touchSlope : Int = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {

        if (event == null) return super.onInterceptTouchEvent(event)
        when (event.action){
            MotionEvent.ACTION_DOWN ->{
                val motionEvent = MotionEvent.obtain(event)
                prevX = motionEvent.x
                motionEvent.recycle()
            }
            MotionEvent.ACTION_MOVE ->{
                val xDiff = abs(event.x - prevX)

                if (xDiff > touchSlope) return false
            }
        }

        return super.onInterceptTouchEvent(event)
    }
}