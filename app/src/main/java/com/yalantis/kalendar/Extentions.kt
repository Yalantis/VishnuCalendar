package com.yalantis.kalendar

import android.content.res.Resources
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import java.util.*
import java.util.Calendar.DAY_OF_WEEK

fun Int.dpToPx(resources: Resources): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    ).toInt()
}

infix fun View.dp(value: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}

fun View.clicks(enabled: Boolean) {
    if (enabled) {
        isClickable = true
        isFocusable = true
    } else {
        isClickable = false
        isFocusable = false
    }
}

fun ViewGroup.applyTransition(block: () -> Unit) {
    TransitionManager.beginDelayedTransition(this)
    block.invoke()
}

fun Float.dpToPx(resources: Resources): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        resources.displayMetrics
    )
}

fun Calendar.getWeekDayName(): String {
    return when (this[DAY_OF_WEEK]) {
        0 -> "Sun"
        1 -> "Mon"
        2 -> "Tue"
        3 -> "Wed"
        4 -> "Thu"
        5 -> "Fri"
        6 -> "Sat"
        else -> ""
    }
}

fun Calendar.getDaysAfter() = when (this[DAY_OF_WEEK]) {
    Calendar.SATURDAY -> 0
    Calendar.FRIDAY -> 1
    Calendar.THURSDAY -> 2
    Calendar.WEDNESDAY -> 3
    Calendar.TUESDAY -> 4
    Calendar.MONDAY -> 5
    Calendar.SUNDAY -> 6
    else -> -1
}

fun Calendar.getDaysBefore() = when (this[DAY_OF_WEEK]) {
    Calendar.SATURDAY -> 6
    Calendar.FRIDAY -> 5
    Calendar.THURSDAY -> 4
    Calendar.WEDNESDAY -> 3
    Calendar.TUESDAY -> 2
    Calendar.MONDAY -> 1
    Calendar.SUNDAY -> 0
    else -> -1
}

fun Calendar.previousMonthName() =
    when (this[Calendar.MONTH]) {
        Calendar.JANUARY -> "December"
        Calendar.FEBRUARY -> "January"
        Calendar.MARCH -> "February"
        Calendar.APRIL -> "March"
        Calendar.MAY -> "April"
        Calendar.JUNE -> "May"
        Calendar.JULY -> "June"
        Calendar.AUGUST -> "July"
        Calendar.SEPTEMBER -> "August"
        Calendar.OCTOBER -> "September"
        Calendar.NOVEMBER -> "October"
        Calendar.DECEMBER -> "November"
        else -> ""
    }

fun Calendar.currentMonthName() =
    when (this[Calendar.MONTH]) {
        Calendar.JANUARY -> "January"
        Calendar.FEBRUARY -> "February"
        Calendar.MARCH -> "March"
        Calendar.APRIL -> "April"
        Calendar.MAY -> "May"
        Calendar.JUNE -> "June"
        Calendar.JULY -> "July"
        Calendar.AUGUST -> "August"
        Calendar.SEPTEMBER -> "September"
        Calendar.OCTOBER -> "October"
        Calendar.NOVEMBER -> "November"
        Calendar.DECEMBER -> "December"
        else -> ""
    }

fun Calendar.nextMonthName() =
    when (this[Calendar.MONTH]) {
        Calendar.JANUARY -> "February"
        Calendar.FEBRUARY -> "March"
        Calendar.MARCH -> "April"
        Calendar.APRIL -> "May"
        Calendar.MAY -> "June"
        Calendar.JUNE -> "July"
        Calendar.JULY -> "August"
        Calendar.AUGUST -> "September"
        Calendar.SEPTEMBER -> "October"
        Calendar.OCTOBER -> "November"
        Calendar.NOVEMBER -> "December"
        Calendar.DECEMBER -> "January"
        else -> ""
    }
