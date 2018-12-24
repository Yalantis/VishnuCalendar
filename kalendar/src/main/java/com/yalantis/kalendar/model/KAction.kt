package com.yalantis.kalendar.model

const val ACTION_NEXT_MONTH = 0
const val ACTION_PREV_MONTH = 1
const val ACTION_SELECT_DAY = 2
const val ACTION_SELECT_DISABLED_DAY = 3

class KAction(val type: Int)