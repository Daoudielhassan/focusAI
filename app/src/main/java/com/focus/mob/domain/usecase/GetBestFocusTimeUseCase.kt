package com.focus.mob.domain.usecase

import com.focus.mob.data.SessionRecord
import java.util.Calendar
import javax.inject.Inject

class GetBestFocusTimeUseCase @Inject constructor() {

    /**
     * Returns the hour of day (0–23) where the user has accumulated the most focus minutes.
     * Returns null if there are no sessions.
     */
    operator fun invoke(sessions: List<SessionRecord>): Int? {
        if (sessions.isEmpty()) return null

        val minutesByHour = IntArray(24)
        val cal = Calendar.getInstance()

        for (session in sessions) {
            cal.timeInMillis = session.timestamp
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            minutesByHour[hour] += session.durationMinutes
        }

        return minutesByHour.indices.maxByOrNull { minutesByHour[it] }
    }
}
