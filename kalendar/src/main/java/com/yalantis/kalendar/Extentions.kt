package com.yalantis.kalendar

import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.MONTH

const val WEEK_OFFSET = 2

const val DAYS_IN_WEEK = 7

const val DEFAULT_DAY_TEXT_SIZE = 18f

const val EMPTY_INT = 0

const val EMPTY_FLOAT = 0f

const val HIDE_MULTIPLIER = 2.5f

const val EMPTY_STRING = ""

const val START_PAGE = 6

const val PAGE_OFFSET = 2

const val KALENDAR_SPEED = 500L

const val ALPHA_INVISIBLE = 0f

const val ALPHA_VISIBLE = 1f

val ALPHA_RANGE = 0f..1f

val DAYS_IN_WEEK_RANGE = 0 until 7

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

fun Calendar.previousMonthName(): String {
    this.add(Calendar.MONTH, -1)
    val pre = this.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    this.add(MONTH, 1)
    return pre
}

fun Calendar.currentMonthName(): String = getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

fun Calendar.nextMonthName(): String {
    this.add(Calendar.MONTH, 1)
    val next = this.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    this.add(Calendar.MONTH, -1)
    return next
}
