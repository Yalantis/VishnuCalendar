package com.yalantis.kalendar.model

import android.content.res.TypedArray
import android.graphics.Typeface
import com.yalantis.kalendar.EMPTY_STRING
import com.yalantis.kalendar.R

class KalendarStylable(attrs: TypedArray) {

    val dragHeight = attrs.getDimensionPixelSize(R.styleable.Kalendar_dragHeight, attrs.resources.getDimensionPixelSize(R.dimen.default_drag_height))
    var dragColor = attrs.getColor(R.styleable.Kalendar_dragColor, attrs.resources.getColor(R.color.light_gray))
    val dragText = attrs.getString(R.styleable.Kalendar_dragText) ?: EMPTY_STRING
    var dragTextColor = attrs.getColor(R.styleable.Kalendar_dragTextColor, attrs.resources.getColor(R.color.drag_text_color))
    val dragTextSize = attrs.getDimensionPixelSize(R.styleable.Kalendar_dragTextSize, attrs.resources.getDimensionPixelSize(R.dimen.drag_text_size))
    var selectedDayDrawable = attrs.getResourceId(R.styleable.Kalendar_selectedDayDrawable, R.drawable.day_background)
    val pageBackground = attrs.getColor(R.styleable.Kalendar_pageBackground, attrs.resources.getColor(android.R.color.white))

    var dayTypeface: Typeface = Typeface.MONOSPACE
    var weekDayTypeface: Typeface = Typeface.DEFAULT
    var monthTypeface: Typeface = Typeface.DEFAULT_BOLD

    var monthSwitchBackground = R.drawable.ic_cell
    var weekDayNamesBackground = R.drawable.ic_cell_1_line

    var selectedWeekBackground = R.drawable.selected_week_back
    var unselectedWeekBackground = R.drawable.unselected_week

    init {
        attrs.recycle()
    }
}