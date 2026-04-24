package com.focus.mob.ml

import com.focus.mob.data.SessionRecord
import java.util.Calendar

/**
 * Feature vector extracted from raw session history.
 * Used by [RuleBasedRecommendationEngine] to produce a [FocusRecommendation].
 */
data class FocusFeatures(
    /** 0–1 scale: fraction of 7h/week achieved in the last 7 days. */
    val energyLevel: Float,
    /** True if the current hour is between 5 and 11 (inclusive). */
    val isMorning: Boolean,
    /** Fraction of days active in the last 7 days (0–1). */
    val historyScore: Float,
    /** Average duration (minutes) of the last 5 sessions, or 30 if no history. */
    val avgSessionMinutes: Double,
    /** Most frequently used ambiance across all sessions, or null if none. */
    val bestAmbiance: String?,
    /** Total focus minutes logged today. */
    val todayMinutes: Int
)

object FeatureExtractor {

    fun extract(sessions: List<SessionRecord>, todayMinutes: Int): FocusFeatures {
        val now = System.currentTimeMillis()
        val weekAgo = now - 7 * 86_400_000L
        val recentSessions = sessions.filter { it.timestamp >= weekAgo }

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isMorning = hour in 5..11

        // Energy level: 7 hours/week = 100 %
        val weekMinutes = recentSessions.sumOf { it.durationMinutes }
        val energyLevel = (weekMinutes / 420f).coerceIn(0f, 1f)

        // Distinct active days in the last 7 days
        val activeDays = recentSessions
            .map { truncateToDay(it.timestamp) }
            .toSet()
            .size
        val historyScore = activeDays / 7f

        // Best ambiance (most frequent across all sessions)
        val bestAmbiance = sessions
            .filter { it.ambiance.isNotBlank() }
            .groupBy { it.ambiance }
            .maxByOrNull { (_, list) -> list.size }
            ?.key

        // Average session duration over the last 5 sessions
        val avgSessionMinutes = if (sessions.isEmpty()) 30.0
        else sessions.take(5).map { it.durationMinutes }.average()

        return FocusFeatures(
            energyLevel      = energyLevel,
            isMorning        = isMorning,
            historyScore     = historyScore,
            avgSessionMinutes = avgSessionMinutes,
            bestAmbiance     = bestAmbiance,
            todayMinutes     = todayMinutes
        )
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
