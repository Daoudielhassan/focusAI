package com.focus.mob.domain.model

data class DailyFocusStat(
    /** Short day label, e.g. "L", "M", "Me", "J", "V", "S", "D" */
    val dayLabel: String,
    val minutes: Int
)
