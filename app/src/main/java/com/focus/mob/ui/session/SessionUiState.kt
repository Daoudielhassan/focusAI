package com.focus.mob.ui.session

data class SessionUiState(
    val sessionId: Long? = null,
    val plannedDurationMinutes: Int = 30,
    val remainingSeconds: Int = 0,
    val elapsedSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val pausedCount: Int = 0,
    val soundTitle: String = "",
    val soundSubtitle: String = "",
    val progress: Float = 0f
)
