package com.yalantis.kalendar.model

import android.content.res.TypedArray
import com.yalantis.kalendar.EMPTY_STRING
import com.yalantis.kalendar.R

class KalendarStylable(attrs: TypedArray) {

    val dragHeight = attrs.getDimensionPixelSize(R.styleable.Kalendar_dragHeight, attrs.resources.getDimensionPixelSize(R.dimen.default_drag_height))
    val dragColor = attrs.getColor(R.styleable.Kalendar_dragColor, attrs.resources.getColor(R.color.light_gray))
    val dragText = attrs.getString(R.styleable.Kalendar_dragText) ?: EMPTY_STRING
    val dragTextColor = attrs.getColor(R.styleable.Kalendar_dragTextColor, attrs.resources.getColor(R.color.drag_text_color))
    val dragTextSize = attrs.getDimensionPixelSize(R.styleable.Kalendar_dragTextSize, attrs.resources.getDimensionPixelSize(R.dimen.drag_text_size))
    val selectedDayDrawable = attrs.getResourceId(R.styleable.Kalendar_selectedDayDrawable, R.drawable.day_background)
    val pageBackground = attrs.getColor(R.styleable.Kalendar_pageBackground, attrs.resources.getColor(android.R.color.white))

    init {
        attrs.recycle()
    }
}