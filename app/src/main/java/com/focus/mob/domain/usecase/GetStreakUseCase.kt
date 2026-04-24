package com.focus.mob.domain.usecase

import com.focus.mob.data.SessionRecord
import java.util.Calendar
import javax.inject.Inject

class GetStreakUseCase @Inject constructor() {

    /**
     * Counts consecutive days ending today that had at least one session.
     */
    operator fun invoke(sessions: List<SessionRecord>): Int {
        if (sessions.isEmpty()) return 0

        val msPerDay = 86_400_000L
        val daySet = sessions
            .map { truncateToDay(it.timestamp) }
            .toSortedSet(reverseOrder())

        var streak   = 0
        var expected = truncateToDay(System.currentTimeMillis())

        for (day in daySet) {
            if (day == expected) {
                streak++
                expected -= msPerDay
            } else if (day < expected) {
                break
            }
        }
        return streak
    }

    private fun truncateToDay(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}
