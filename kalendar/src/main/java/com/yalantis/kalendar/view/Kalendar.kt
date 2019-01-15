package com.yalantis.kalendar.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.ViewPager
import com.yalantis.kalendar.MonthPagerAdapter
import com.yalantis.kalendar.PAGE_OFFSET
import com.yalantis.kalendar.R
import com.yalantis.kalendar.START_PAGE
import com.yalantis.kalendar.model.KalendarStylable
import java.util.*

class Kalendar(context: Context, val attributeSet: AttributeSet) : FrameLayout(context, attributeSet),
    MonthPage.KalendarListener {

    private lateinit var viewPager: ViewPager

    private var isFirstMonthInit = false

    private lateinit var stylable: KalendarStylable

    private var previousPage = START_PAGE

    private var currentMonth: MonthPage? = null

    var changeListener: MonthPage.KalendarListener? = null

    private val scrollListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            when (position) {
                1 -> refreshAdapterFront()
                viewPager.adapter?.count?.minus(2) -> refreshAdapterBack()
            }
            makeWrapContent(position)
            updateMonth(position)
        }
    }

    @DrawableRes
    var selectedDayDrawable: Int = R.drawable.day_background
        set(value) {
            field = value
            stylable.selectedDayDrawable = value
        }

    @ColorRes
    var dragColor = R.color.light_gray
        set(value) {
            field = value
            stylable.dragColor = value
        }

    @ColorRes
    var dragTextColor = R.color.drag_text_color
        set(value) {
            field = value
            stylable.dragTextColor = value
        }

    var dayTypeface: Typeface = Typeface.MONOSPACE
        set(value) {
            field = value
            stylable.dayTypeface = value
        }

    var weekDayTypeface: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            stylable.weekDayTypeface = value
        }

    var monthTypeface: Typeface = Typeface.DEFAULT_BOLD
        set(value) {
            field = value
            stylable.monthTypeface = value
        }

    @DrawableRes
    var monthSwitchBackground = R.drawable.ic_cell
        set(value) {
            field = value
            stylable.monthSwitchBackground = value
        }

    @DrawableRes
    var selectedWeekBackground = R.drawable.selected_week_back
        set(value) {
            field = value
            stylable.selectedWeekBackground = value
        }

    @DrawableRes
    var unselectedWeekBackground = R.drawable.unselected_week
        set(value) {
            field = value
            stylable.unselectedWeekBackground = value
        }

    @DrawableRes
    var weekDayNamesBackground = R.drawable.ic_cell_1_line
        set(value) {
            field = value
            stylable.weekDayNamesBackground = value
        }

    private fun updateMonth(position: Int) {
        when {
            previousPage > position -> {
                changeListener?.onMonthChanged(false)
            }
            previousPage < position -> {
                changeListener?.onMonthChanged(true)
            }
        }
        previousPage = position
    }

    init {
        parseStyledAttributes()
        createView()
    }

    /**
     * This method set view pager's size to wrap content on each page selection
     */

    private fun makeWrapContent(position: Int) {
        val adapter = viewPager.adapter as MonthPagerAdapter
        currentMonth= adapter.getPageAt(position)
        viewPager.layoutParams = viewPager.layoutParams.apply {
            this.height = currentMonth?.getCurrentHeight() ?: WRAP_CONTENT
        }
    }

    /**
     * This method add new months to view pager's adapter start
     */

    private fun refreshAdapterFront() {
        val adapter = viewPager.adapter as MonthPagerAdapter
        adapter.addToStart(monthsToStart())
        viewPager.currentItem = START_PAGE + 1
    }

    /**
     * This method add new months to view pager's adapter back
     */

    private fun refreshAdapterBack() {
        val adapter = viewPager.adapter as MonthPagerAdapter
        val currentItem = viewPager.currentItem
        adapter.addToEnd(monthsToEnd())
        viewPager.currentItem = currentItem
    }

    /**
     * This method creates new months to view pager's adapter start
     */

    private fun monthsToStart(): List<Date> {
        val adapter = viewPager.adapter as MonthPagerAdapter
        val firstDate = adapter.getFirstDate()
        val calendar = Calendar.getInstance()
        calendar.time = firstDate
        return (0 until 6).map {
            calendar.add(Calendar.MONTH, -1)
            calendar.time
        }.toList()
    }

    /**
     * This method creates new months to view pager's adapter back
     */

    private fun monthsToEnd(): List<Date> {
        val adapter = viewPager.adapter as MonthPagerAdapter
        val lastDate = adapter.getLastDate()
        val calendar = Calendar.getInstance()
        calendar.time = lastDate
        return (0 until 6).map {
            calendar.add(Calendar.MONTH, 1)
            calendar.time
        }.toList()
    }

    /**
     * Initializing view pager for root view
     */

    private fun createViewPager(): View? {
        viewPager = ViewPager(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            addOnPageChangeListener(scrollListener)
            adapter = MonthPagerAdapter(this@Kalendar).apply {
                stylable = this@Kalendar.stylable
                setMonths(createYear(Calendar.getInstance().time))
            }
        }
        viewPager.offscreenPageLimit = PAGE_OFFSET
        viewPager.currentItem = START_PAGE
        return viewPager
    }


    /**
     * Method allow you to force collapse current page
     */

    fun collapse() {
        val currentPage = (viewPager.adapter as MonthPagerAdapter).getPageAt(viewPager.currentItem)
        (currentPage as MonthPage).collapse()
    }

    /**
     * Method allow you to force expand current page
     */

    fun expand() {
        val currentPage = (viewPager.adapter as MonthPagerAdapter).getPageAt(viewPager.currentItem)
        (currentPage as MonthPage).expand()
    }

    /**
     * Method allow you to get page by it position in adapter
     *  Remember: adapter positions rewrites after view pager reach first position
     */

    fun getMonthAt(position: Int) = (viewPager.adapter as MonthPagerAdapter).getPageAt(position)

    /**
     * Method allow you to force swipe month
     *
     */

    fun swipeMonth(isSwipeToEnd: Boolean) {
        if (isSwipeToEnd) {
            viewPager.arrowScroll(ViewPager.FOCUS_RIGHT)
        } else {
            viewPager.arrowScroll(ViewPager.FOCUS_LEFT)
        }
    }

    /**
     * Method allow you to get date from current selected page
     */

    fun getSelectedPageDate(): Date {
        val currentPage = (viewPager.adapter as MonthPagerAdapter).getPageAt(viewPager.currentItem)
        return (currentPage as MonthPage).getCurrentDate()
    }

    /**
     * Method creates initial year, returns back on 7 month and iterate forward for 12 month
     */

    private fun createYear(from: Date): List<Date> {
        val calendar = Calendar.getInstance()
        calendar.time = from
        calendar.add(Calendar.MONTH, -7)
        return (0 until 12).map {
            calendar.add(Calendar.MONTH, 1)
            calendar.time
        }.toList()
    }

    /**
     * Method creates object with style attributes for each page
     */

    private fun parseStyledAttributes() {
        stylable = KalendarStylable(context.obtainStyledAttributes(attributeSet, R.styleable.Kalendar))
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams = layoutParams.apply { this.height = WRAP_CONTENT }
    }

    /**
     * Method creates view pager as child of root element
     */

    private fun createView() {
        addView(createViewPager())
    }

    override fun onSizeMeasured(monthPage: MonthPage, collapsedHeight: Int, totalHeight: Int) {
        if (isFirstMonthInit.not()) {
            isFirstMonthInit = true
            makeWrapContent(6)
        }
        changeListener?.onSizeMeasured(monthPage, collapsedHeight, totalHeight)
    }

    override fun onDayClick(date: Date) {
        changeListener?.onDayClick(date)
        Log.e("TAG", date.toString())
    }

    override fun onStateChanged(isCollapsed: Boolean) {
        changeListener?.onStateChanged(isCollapsed)
    }

    override fun onHeightChanged(newHeight: Int) {
        viewPager.layoutParams = viewPager.layoutParams.apply { height = newHeight }
        changeListener?.onHeightChanged(newHeight)
    }

    override fun onMonthChanged(forward: Boolean, date: Date?) {
        val adapter = (viewPager.adapter as MonthPagerAdapter)

        val page = if (forward) {
            viewPager.arrowScroll(ViewPager.FOCUS_RIGHT)
            adapter.getPageAt(viewPager.currentItem)
        } else {
            viewPager.arrowScroll(ViewPager.FOCUS_LEFT)
            adapter.getPageAt(viewPager.currentItem)
        }

        date?.let { page?.selectDay(it) }

        changeListener?.onMonthChanged(forward, date)
    }

}