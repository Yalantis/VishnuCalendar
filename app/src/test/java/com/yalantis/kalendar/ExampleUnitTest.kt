package com.yalantis.kalendar

import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val calendar = Calendar.getInstance()
        for (i in 0 until 7) {
            // mb day of week
            println("Day of week: " + calendar[Calendar.DAY_OF_WEEK])
            if (calendar[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_WEEK, -1)
            } else break
        }

        val startDay = calendar[Calendar.DAY_OF_MONTH]
        println("StartDay: $startDay")
    }
}
