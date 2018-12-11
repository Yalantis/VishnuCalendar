package com.yalantis.kalendar

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView

class Month(context: Context) : TextView(context) {
    companion object {
        const val TYPE_LEFT = 0
        const val TYPE_MID = 1
        const val TYPE_RIGHT = 2
    }

    init {
        layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            weight = 1f
        }
    }

    var click: (() -> Unit)? = null
        set(value) {
            field = value
            setOnClickListener { click?.invoke() }
        }

    var label = ""
        set(value) {
            field = value
            text = value
        }
    var type = TYPE_MID
        set(value) {
            typeface = Typeface.DEFAULT_BOLD
            when (value) {
                TYPE_LEFT -> {
                    textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
                    alpha = 0.5f
                }
                TYPE_MID -> {
                    textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    text
                }
                TYPE_RIGHT -> {
                    textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
                    alpha = 0.5f
                }
            }
        }
}