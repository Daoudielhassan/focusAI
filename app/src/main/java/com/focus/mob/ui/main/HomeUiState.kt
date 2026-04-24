package com.focus.mob.ui.main

data class HomeUiState(
    val selectedDuration: Int = 30,
    val todayFocusMinutes: Int = 0,
    val todaySessions: Int = 0,
    val streakDays: Int = 0,
    val focusScore: Int = 0,
    val recommendedDuration: Int = 30,
    val recommendedSoundCategory: String = "ambient",
    val aiTip: String = "",
    val currentQuote: String = "\"La concentration est la racine de toute réussite.\""
)
