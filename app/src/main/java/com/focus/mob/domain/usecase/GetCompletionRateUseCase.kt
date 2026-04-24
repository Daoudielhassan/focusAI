package com.focus.mob.domain.usecase

import com.focus.mob.data.SessionRecord
import java.util.Calendar
import javax.inject.Inject

class GetCompletionRateUseCase @Inject constructor() {

    /**
     * Completion rate = distinct days in the last 7 that had ≥ 1 session / 7.
     * Range: 0f..1f
     */
    operator fun invoke(sessions: List<SessionRecord>): Float {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val msPerDay   = 86_400_000L
        val weekStart  = todayStart - 6 * msPerDay

        val activeDays = sessions
            .filter { it.timestamp >= weekStart }
            .map { (it.timestamp / msPerDay) }   // truncate to day bucket
            .toSet()
            .size

        return activeDays / 7f
    }
}
