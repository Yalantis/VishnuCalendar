package com.yalantis.kalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yalantis.kalendar.view.Kalendar
import java.util.*

class Obertka : Fragment() {

    companion object {
        private const val MONTH_TYPE = "type"

        fun newInstance(which: Int): Obertka {
            return Obertka().apply {
                arguments = Bundle().apply {
                    putInt(MONTH_TYPE, which)
                }
            }
        }

        const val PREVIOUS = 0
        const val CURRENT = 1
        const val NEXT = 2
    }

    private var monthType = CURRENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monthType = arguments?.getInt(MONTH_TYPE) ?: CURRENT
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return Kalendar(context!!)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (monthType) {
            CURRENT -> (view as Kalendar).setDate(getCurrentDate())
            PREVIOUS -> (view as Kalendar).setDate(getPreviousMonthDate())
            NEXT -> (view as Kalendar).setDate(getNextMonthDate())
        }
    }


    private fun getPreviousMonthDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val prevDate = calendar.time
        calendar.add(Calendar.MONTH, 1)
        return prevDate
    }


    private fun getNextMonthDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        val nextDate = calendar.time
        calendar.add(Calendar.MONTH, -1)
        return nextDate
    }

    private fun getCurrentDate() = Calendar.getInstance().time

}