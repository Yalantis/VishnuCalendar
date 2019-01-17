package com.yalantis.vishnu.model

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.yalantis.vishnu.EMPTY_STRING
import com.yalantis.vishnu.R

class VishnuStylable(private var context: Context?, attrs: TypedArray) {

    val dragHeight = attrs.getDimensionPixelSize(R.styleable.Vishnu_dragHeight, attrs.resources.getDimensionPixelSize(R.dimen.default_drag_height))
    var dragColor = attrs.getColor(R.styleable.Vishnu_dragColor, ContextCompat.getColor(requireNotNull(context), R.color.light_gray))
    val dragText = attrs.getString(R.styleable.Vishnu_dragText) ?: EMPTY_STRING
    var dragTextColor = attrs.getColor(R.styleable.Vishnu_dragTextColor, ContextCompat.getColor(requireNotNull(context),R.color.drag_text_color))
    val dragTextSize = attrs.getDimensionPixelSize(R.styleable.Vishnu_dragTextSize, attrs.resources.getDimensionPixelSize(R.dimen.drag_text_size))
    var selectedDayDrawable = attrs.getResourceId(R.styleable.Vishnu_selectedDayDrawable, R.drawable.day_background)
    val pageBackground = attrs.getColor(R.styleable.Vishnu_pageBackground, ContextCompat.getColor(requireNotNull(context),android.R.color.white))

    var dayTypeface: Typeface = Typeface.MONOSPACE
    var weekDayTypeface: Typeface = Typeface.DEFAULT
    var monthTypeface: Typeface = Typeface.DEFAULT_BOLD

    var monthSwitchBackground = R.drawable.ic_cell
    var weekDayNamesBackground = R.drawable.ic_cell_1_line

    var selectedWeekBackground = R.drawable.selected_week_back
    var unselectedWeekBackground = R.drawable.unselected_week

    init {
        attrs.recycle()
        context = null
    }
}