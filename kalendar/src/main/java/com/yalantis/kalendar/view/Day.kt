package com.yalantis.kalendar.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

class Day(context: Context) : TextView(context) {
    var label = ""
        set(value) {
            field = value
            text = value
        }
    var date = Date()

    var clickListener: OnDayClickListener? = null

    var canClick = false
        set(value) {
            field = value
            setOnClickListener {
                if (value) {
                    clickListener?.onNormalDayClick(it as Day)
                } else {
                    clickListener?.onDisabledDayClick(it as Day)
                }
            }
        }

    var labelColor = 0
        set(value) {
            field = value
            setTextColor(value)
        }

    init {
        isClickable = true
        isFocusable = true
        gravity = Gravity.CENTER
        textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
    }

    fun size(width: Int, height: Int) {
        layoutParams = LinearLayout.LayoutParams(width, height).apply {
            weight = 1f
        }
    }

    interface OnDayClickListener {
        fun onNormalDayClick(day: Day)

        fun onDisabledDayClick(day: Day)
    }
}
