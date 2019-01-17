package com.yalantis.vishnu.interfaces

import java.util.*

interface DateManager {

    /**
     * Set date with displaying it
     */

    fun setDate(date: Date)

    /**
     * Add one day to current date
     */

    fun addDay()

    /**
     * Add one day to current date
     */

    fun getDayLabel(): String

    /**
     * Provide current selected date
     */

    fun getCurrentDate(): Date

    /**
     * Provide current month name
     */

    fun getCurrentMonthLabel(): String

    /**
     * Provide previous month name
     */

    fun getPreviousMonthLabel(): String

    /**
     * Provide next month name
     */

    fun getNextMonthLabel(): String

    /**
     * Switch from current month to one month forward
     */

    fun goNextMonth(where: Date? = null)

    /**
     * Switch from current month to one month backward
     */

    fun goPreviousMonth(where: Date? = null)

    /**
     * Set date without displaying it
     */

    fun setCurrentDate(day: Date)

    /**
     * Provide week day name
     */

    fun getWeekDayName(which: Int): String
}