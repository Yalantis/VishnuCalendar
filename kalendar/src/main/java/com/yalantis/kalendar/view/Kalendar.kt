package com.yalantis.kalendar.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.yalantis.kalendar.MonthPagerAdapter
import java.util.*

class Kalendar(context: Context, val attributeSet: AttributeSet)
    : FrameLayout(context, attributeSet), MonthPage.KalendarListener {

    private lateinit var viewPager: ViewPager

    var changeListener: MonthPage.KalendarListener? = null

    private val scrollListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            when (position) {
                1 -> refreshAdapterFront()
                viewPager.adapter?.count?.minus(2) -> refreshAdapterBack()
            }
        }
    }

    private fun refreshAdapterFront() {
        val adapter = (viewPager.adapter as MonthPagerAdapter)
        val startDate = adapter.getFirstDate()
        val halfYear = createYear(startDate)
        adapter.addMonths(halfYear)
        viewPager.currentItem = 7
    }

    private fun refreshAdapterBack() {
        val adapter = (viewPager.adapter as MonthPagerAdapter)
        val startDate = adapter.getLastDate()
        val halfYear = createYear(startDate)
        adapter.addMonths(halfYear)
        viewPager.currentItem = 5
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams = layoutParams.apply { this?.height = WRAP_CONTENT }
        createView()
    }

    private fun createViewPager(): View? {
        viewPager = ViewPager(context).apply {
            id = View.generateViewId()
            addOnPageChangeListener(scrollListener)
            adapter = MonthPagerAdapter(this@Kalendar, attributeSet).apply {
                addMonths(createYear(Calendar.getInstance().time))
            }
        }
        viewPager.currentItem = 6
        return viewPager
    }


    /**
     * Method allow you to force collapse view
     */

    fun collapse() {
        val currentPage = (viewPager.adapter as MonthPagerAdapter).getPageAt(viewPager.currentItem)
        (currentPage as MonthPage).collapse()
    }

    /**
     * Method allow you to force expand view
     */

    fun expand() {
        val currentPage = (viewPager.adapter as MonthPagerAdapter).getPageAt(viewPager.currentItem)
        (currentPage as MonthPage).expand()
    }

    private fun createYear(from: Date): List<Date> {
        val calendar = Calendar.getInstance()
        calendar.time = from
        calendar.add(Calendar.MONTH, -7)
        return (0 until 12).map {
            calendar.add(Calendar.MONTH, 1)
            calendar.time
        }.toList()
    }

    private fun createView() {
        addView(LinearLayout(context).apply {
            addView(createViewPager())
        })
    }

    override fun onDayClick(date: Date) {
        changeListener?.onDayClick(date)
    }

    override fun onStateChanged(isCollapsed: Boolean) {
        changeListener?.onStateChanged(isCollapsed)
    }

    override fun onHeightChanged(newHeight: Int) {
        layoutParams = layoutParams.apply { this?.height = newHeight }
        changeListener?.onHeightChanged(newHeight)
    }

    override fun onMonthChanged(forward: Boolean) {
        if (forward) {
            viewPager.arrowScroll(ViewPager.FOCUS_RIGHT)
        } else {
            viewPager.arrowScroll(ViewPager.FOCUS_LEFT)
        }
        changeListener?.onMonthChanged(forward)
    }

}