package com.focus.mob.domain.usecase

import com.focus.mob.data.SessionRecord
import com.focus.mob.domain.model.DailyFocusStat
import java.util.Calendar
import javax.inject.Inject

class GetWeeklyFocusStatsUseCase @Inject constructor() {

    // French day abbreviations: L M Me J V S D
    private val DAY_LABELS = arrayOf("D", "L", "M", "Me", "J", "V", "S")

    /**
     * Returns 7 [DailyFocusStat] entries ordered from 6 days ago (index 0) to today (index 6).
     */
    operator fun invoke(sessions: List<SessionRecord>): List<DailyFocusStat> {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val msPerDay = 86_400_000L
        val minutesPerDay = IntArray(7)   // index 0 = 6 days ago, index 6 = today

        for (session in sessions) {
            val diff    = todayStart - session.timestamp
            val daysAgo = when {
                diff < 0          -> 0            // today but after midnight → index 6
                else              -> (diff / msPerDay).toInt() + if (session.timestamp < todayStart) 1 else 0
            }
            if (daysAgo in 0..6) {
                minutesPerDay[6 - daysAgo] += session.durationMinutes
            }
        }

        return (0..6).map { i ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = todayStart - (6 - i) * msPerDay
            }
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)   // 1=Sun .. 7=Sat
            DailyFocusStat(
                dayLabel = DAY_LABELS[dayOfWeek - 1],
                minutes  = minutesPerDay[i]
            )
        }
    }
}
