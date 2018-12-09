package com.yalantis.kalendar

import android.content.res.Resources
import android.util.TypedValue
import java.util.*
import java.util.Calendar.DAY_OF_WEEK

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

fun Calendar.currentMonthName() =
    when (this[Calendar.MONTH] + 1) {
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
        else -> "January"
    }
