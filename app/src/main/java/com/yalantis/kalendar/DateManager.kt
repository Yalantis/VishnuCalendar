package com.yalantis.kalendar

import java.util.*

interface DateManager {

    fun setDate(date: Date)

    fun addDay()

    fun getDayLabel(): String

    fun getCurrentDate(): Date

    fun getCurrentMonthLabel(): String

    fun getPreviousMonthLabel(): String

    fun getNextMonthLabel(): String

    fun goNextMonth()

    fun goPreviousMonth()

    fun selectDay(day: Day)
}