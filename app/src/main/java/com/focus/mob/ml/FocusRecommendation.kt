package com.focus.mob.ml

data class FocusRecommendation(
    val focusScore: Int,
    val recommendedDuration: Int,
    val recommendedSoundCategory: String,
    val message: String
)
