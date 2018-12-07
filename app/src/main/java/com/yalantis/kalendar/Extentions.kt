package com.yalantis.kalendar

import android.content.res.Resources
import android.util.TypedValue

fun Float.dpToPx(resources: Resources): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        resources.displayMetrics
    )
}