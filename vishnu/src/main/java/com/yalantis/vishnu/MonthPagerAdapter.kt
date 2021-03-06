package com.yalantis.vishnu

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.yalantis.vishnu.interfaces.VishnuListener
import com.yalantis.vishnu.model.VishnuStylable
import com.yalantis.vishnu.view.MonthPage
import java.util.*

class MonthPagerAdapter(private val listener: VishnuListener) : PagerAdapter() {

    private val months = mutableListOf<Date>()

    private val pages = SparseArray<MonthPage>()

    lateinit var stylable: VishnuStylable

    override fun isViewFromObject(p0: View, p1: Any) = p0 === p1

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val kalendar = MonthPage(container.context, stylable).apply {
            pageDate = months[position]
            listener = this@MonthPagerAdapter.listener
        }
        pages.put(position, kalendar)
        container.addView(kalendar)
        return kalendar
    }

    override fun getCount() = months.size

    override fun destroyItem(container: ViewGroup, position: Int, month: Any) {
        val page = month as MonthPage
        container.removeView(page)
    }

    fun getPageAt(pos: Int): MonthPage?  = pages[pos]

    fun getLastDate() = months.last()

    fun getFirstDate() = months.first()

    fun addToEnd(month: List<Date>) {
        months.addAll(month)
        notifyDataSetChanged()
    }

    fun addToStart(month: List<Date>) {
        month.forEach { months.add(0, it) }
        notifyDataSetChanged()
    }

    fun setMonths(month: List<Date>) {
        months.clear()
        months.addAll(month)
        notifyDataSetChanged()
    }

}