package com.focus.mob.ml

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleBasedRecommendationEngine @Inject constructor() {

    fun recommend(features: FocusFeatures): FocusRecommendation {
        val score    = computeScore(features)
        val duration = recommendDuration(features)
        val sound    = recommendSound(features)
        val message  = buildMessage(features, duration)

        return FocusRecommendation(
            focusScore                = score,
            recommendedDuration       = duration,
            recommendedSoundCategory  = sound,
            message                   = message
        )
    }

    // ── Score : energy 60 % + history 40 % ──────────────────────────────────
    private fun computeScore(f: FocusFeatures): Int =
        ((f.energyLevel * 0.6f + f.historyScore * 0.4f) * 100)
            .toInt()
            .coerceIn(0, 100)

    // ── Duration rules ───────────────────────────────────────────────────────
    private fun recommendDuration(f: FocusFeatures): Int = when {
        f.energyLevel < 0.25f                  -> 15   // faible énergie → session courte
        f.isMorning && f.historyScore >= 0.5f  -> 45   // matin + bon historique → session soutenue
        f.avgSessionMinutes < 20               -> 15   // sessions récentes très courtes
        f.avgSessionMinutes < 40               -> 30   // durée typique
        else                                   -> 60   // utilisateur régulier et efficace
    }

    // ── Sound rules ──────────────────────────────────────────────────────────
    private fun recommendSound(f: FocusFeatures): String =
        f.bestAmbiance?.takeIf { it.isNotBlank() } ?: "ambient"

    // ── Message rules ────────────────────────────────────────────────────────
    private fun buildMessage(f: FocusFeatures, duration: Int): String = when {
        f.energyLevel < 0.25f ->
            "Votre niveau d'énergie est bas. Une courte session de $duration min vous aidera à vous relancer."
        f.isMorning && f.historyScore >= 0.5f ->
            "Vos sessions du matin sont généralement plus stables. Profitez de cet élan !"
        f.avgSessionMinutes < 20 ->
            "Vos sessions récentes sont courtes — restez régulier, la durée augmentera naturellement."
        f.historyScore >= 0.7f ->
            "Excellente régularité cette semaine. Maintenez le cap !"
        f.todayMinutes == 0 ->
            "Aucune session aujourd'hui. Commencez avec $duration min pour prendre de l'élan."
        else ->
            "Continuez sur votre lancée. Une session de $duration min vous attend."
    }
}
