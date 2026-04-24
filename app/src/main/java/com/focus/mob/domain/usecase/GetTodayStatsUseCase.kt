package com.focus.mob.domain.usecase

import com.focus.mob.data.SessionRecord
import java.util.Calendar
import javax.inject.Inject

data class TodayStats(val minutes: Int, val sessions: Int)

class GetTodayStatsUseCase @Inject constructor() {

    operator fun invoke(sessions: List<SessionRecord>): TodayStats {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todaySessions = sessions.filter { it.timestamp >= startOfDay }
        return TodayStats(
            minutes  = todaySessions.sumOf { it.durationMinutes },
            sessions = todaySessions.size
        )
    }
}
