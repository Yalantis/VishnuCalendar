package com.yalantis.kalendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView

const val DRAG_HEIGHT = 30

class Kalendar(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet), ViewProvider,
    View.OnTouchListener {

    private var totalWidth: Int = 0

    private var totalHeight: Int = 0

    private var dayContainerHeight = 0

    private var weeksMarginTop = 0

    private var previousSelectedDay: View? = null

    private var isCreated = false

    private val dragView = createDragView()

    private val moveManager by lazy { MoveManager(this) }

    private fun createWeekContainer(): LinearLayout {
        val weekContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        return weekContainer
    }

    private fun createWeeks() {
        for (i in 0 until 5) {
            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL

                    setBackgroundResource(android.R.color.holo_green_dark)

                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                        setMargins(0, weeksMarginTop, 0, 0)
                    }

                    for (j in 0 until 7) {
                        this.addView(createDay(i.toString()))
                    }
                }
            )
        }
    }

    private fun createDay(label: String = "8"): TextView {
        return TextView(context).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                changeColors(it)
                selectWeek(it)
            }
            setBackgroundResource(R.drawable.day_background)
            text = label
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
            setTextColor(resources.getColor(android.R.color.background_dark))
            layoutParams = LinearLayout.LayoutParams(totalWidth / 7, dayContainerHeight)
        }
    }

    private fun selectWeek(selectedDay: View?) {
        selectedDay?.let {
            val parent = it.rootView
            val selectedWeek = indexOfChild(parent)
            moveManager?.setSelectedWeek(selectedWeek)
        }
    }

    private fun changeColors(newSelectedDay: View?) {
        newSelectedDay?.setBackgroundResource(R.drawable.day_background)
        previousSelectedDay?.background = null
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isCreated.not()) {
            calculateBounds(left, top, right, bottom)
            orientation = VERTICAL
            setBackgroundResource(android.R.color.holo_purple)
            setOnTouchListener(this)
            createMonthSwitch()
            createWeekDays()
            createWeeks()
            createDragView()
            addView(dragView)
            isCreated = true
        }
    }

    private fun createDragView(): View {
        return LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, DRAG_HEIGHT)
            setBackgroundColor(Color.GRAY)
        }
    }

    private fun createMonthSwitch() {
        addView(LinearLayout(context).apply {
            addView(createLeftMonth("March"))
            addView(createCenterMonth("April"))
            addView(createRightMonth("May"))
        })
    }

    private fun createLeftMonth(label: String): View? {
        return TextView(context).apply {
            text = label
            textSize = 25f
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 1f
                gravity = Gravity.START
            }
        }
    }

    private fun createCenterMonth(label: String): View? {
        return TextView(context).apply {
            text = label
            textSize = 25f
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 3f
                gravity = Gravity.CENTER
            }
        }
    }

    private fun createRightMonth(label: String): View? {
        return TextView(context).apply {
            text = label
            textSize = 25f
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 1f
                gravity = Gravity.END
            }
        }
    }

    private fun calculateBounds(left: Int, top: Int, right: Int, bottom: Int) {
        totalWidth = right - left
        totalHeight = bottom - top
        dayContainerHeight = totalHeight / 10
        weeksMarginTop = totalHeight / 40
    }

    private fun createWeekDays() {
        addView(LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(createDay("Mon"))
            addView(createDay("Tue"))
            addView(createDay("Wed"))
            addView(createDay("Thu"))
            addView(createDay("Fri"))
            addView(createDay("Sat"))
            addView(createDay("Sun"))
        })
    }

    override fun onTouch(v: View?, event: MotionEvent) = moveManager.onTouch(event)

    override fun changeViewBottom(newBottom: Int) {
        bottom = newBottom
    }

    override fun changeDragTop(newDragTop: Float) {
        dragView.y = newDragTop
    }

    override fun getDragViewTop() = dragView.y

    override fun getWeekHeight(position: Int) = getChildAt(position).height

    override fun getWeekBottom(position: Int) = getChildAt(position).y + getChildAt(position).height

    override fun getWeekTop(position: Int) = getChildAt(position).y

    override fun setWeekTop(position: Int, newTop: Float) {
        getChildAt(position).y = newTop
    }

}