package com.focus.mob.ui.main
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.focus.mob.data.AppDatabase
import com.focus.mob.databinding.ActivityInsightsIaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class InsightsIaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsightsIaBinding

    // Réponses de Lumina selon les mots-clés
    private val aiResponses = mapOf(
        "bilan" to "D'après vos données, vous avez accumulé un temps de focus solide cette semaine. Continuez sur cette lancée — la régularité est la clé de la progression ! 📊",
        "conseil" to "Essayez de placer vos sessions les plus longues avant midi, quand votre cortisol est naturellement plus élevé. Utilisez la musique ambient pour étendre votre flow. 💡",
        "objectif" to "Je recommande de viser 2h de focus profond par jour. Commencez par 3 sessions de 30min, puis augmentez progressivement. Vous pouvez y arriver ! 🎯",
        "musique" to "Les études montrent que la musique sans paroles augmente la productivité de 10-15%. Continuez à utiliser l'ambiance sonore pendant vos sessions ! 🎵",
        "distraction" to "Pour minimiser les distractions, essayez la règle des 2 minutes : si une pensée parasite surgit, notez-la et revenez à votre tâche. Votre cerveau s'entraîne comme un muscle. 🧠",
        "fatigue" to "La fatigue est un signal. Après 90min de concentration, votre cerveau entre naturellement en mode récupération. Prenez des pauses courtes de 5-10min entre vos sessions. 😴",
    )

    private val defaultResponse = "C'est une excellente question ! D'après votre profil de focus, je recommande d'expérimenter différentes durées de session pour trouver votre rythme optimal. N'hésitez pas à me poser d'autres questions ! ✨"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsightsIaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationUtils.setupBottomNavigation(this, NavigationUtils.Tab.INSIGHTS_IA)

        loadRealStats()
        setupInsightCardAnimations()
        setupChat()
    }

    // ═══════════════════════════════════════
    // REAL STATS FROM ROOM DB
    // ═══════════════════════════════════════

    private fun loadRealStats() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val dao = AppDatabase.getDatabase(applicationContext).sessionDao()
                val sessions = dao.getAllSessions()

                val totalSessions = sessions.size
                val totalMinutes = sessions.sumOf { it.durationMinutes }.toLong()
                val bestMin = sessions.maxOfOrNull { it.durationMinutes.toLong() } ?: 0L

                // Score focus : formule basée sur la régularité et la durée totale
                val focusScore = when {
                    totalSessions == 0 -> 0
                    else -> minOf(100, (totalSessions * 10 + (totalMinutes / 10)).toInt())
                }

                withContext(Dispatchers.Main) {
                    // Hide shimmer, show content
                    binding.layoutShimmer.root.visibility = android.view.View.GONE
                    binding.scrollInsights.visibility = android.view.View.VISIBLE

                    // Animate counter for sessions
                    animateCounter(0, totalSessions) { binding.tvTotalSessions.text = it.toString() }

                    // Total time
                    val hours = totalMinutes / 60
                    val mins = totalMinutes % 60
                    binding.tvTotalTime.text = if (hours > 0) "${hours}h${mins}m" else "${mins}m"

                    // Best session
                    binding.tvBestSession.text = if (bestMin > 0) "${bestMin}m" else "--"

                    // Focus score with animation
                    animateCounter(0, focusScore) { binding.tvFocusScore.text = it.toString() }

                    // Update dynamic insight texts based on data
                    if (totalSessions > 0) {
                        updateInsightCards(totalSessions, totalMinutes, bestMin)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.layoutShimmer.root.visibility = android.view.View.GONE
                    binding.scrollInsights.visibility = android.view.View.VISIBLE
                    binding.tvTotalSessions.text = "0"
                    binding.tvTotalTime.text = "0m"
                    binding.tvBestSession.text = "--"
                    binding.tvFocusScore.text = "0"
                }
            }
        }
    }

    private fun updateInsightCards(totalSessions: Int, totalMinutes: Long, bestMin: Long) {
        // Insight 1 : basé sur les sessions
        if (totalSessions >= 3) {
            binding.tvInsight1Body.text =
                "Avec $totalSessions sessions complétées, vous avez développé une vraie routine. La régularité est votre force !"
        }
        // Insight 2 : basé sur la durée totale
        if (totalMinutes > 60) {
            binding.tvInsight2Body.text =
                "Vous avez concentré ${totalMinutes}min de focus total. C'est l'équivalent de ${totalMinutes / 60}h de travail profond !"
        }
        // Insight 3 : meilleure session
        if (bestMin > 0) {
            binding.tvInsight3Body.text =
                "Votre meilleure session est de ${bestMin}min. Essayez de battre ce record lors de votre prochaine session !"
        }
    }

    // ═══════════════════════════════════════
    // INSIGHT CARD ANIMATIONS (slide in)
    // ═══════════════════════════════════════

    private fun setupInsightCardAnimations() {
        val cards = listOf(binding.cardInsight1, binding.cardInsight2, binding.cardInsight3)
        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.translationY = 30f
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((index * 150 + 200).toLong())
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    // ═══════════════════════════════════════
    // LUMINA CHAT
    // ═══════════════════════════════════════

    private fun setupChat() {
        // Quick chips
        binding.chipQ1.setOnClickListener { sendMessage("bilan") }
        binding.chipQ2.setOnClickListener { sendMessage("conseil") }
        binding.chipQ3.setOnClickListener { sendMessage("objectif") }

        // Send button
        binding.btnSend.setOnClickListener {
            val text = binding.etChatInput.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                binding.etChatInput.setText("")
                hideKeyboard()
            }
        }

        // IME action
        binding.etChatInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.btnSend.performClick()
                true
            } else false
        }
    }

    private fun sendMessage(query: String) {
        // Show user message
        binding.layoutUserMessage.visibility = android.view.View.VISIBLE
        binding.tvUserMessage.text = query
        binding.tvUserMessage.animate().alpha(0f).setDuration(0).start()
        binding.tvUserMessage.animate().alpha(1f).setDuration(300).start()

        // Show typing indicator
        binding.layoutTyping.visibility = android.view.View.VISIBLE
        startTypingAnimation()

        // Scroll to bottom
        binding.scrollInsights.post {
            binding.scrollInsights.fullScroll(android.view.View.FOCUS_DOWN)
        }

        // Simulate AI thinking delay then respond
        lifecycleScope.launch {
            delay(1800)
            val response = findResponse(query)

            // Hide typing, show response
            binding.layoutTyping.visibility = android.view.View.GONE
            binding.tvAiMessage.animate().alpha(0f).setDuration(200).withEndAction {
                binding.tvAiMessage.text = response
                binding.tvAiMessage.animate().alpha(1f).setDuration(400).start()
            }.start()

            // Scroll again after response
            delay(100)
            binding.scrollInsights.post {
                binding.scrollInsights.fullScroll(android.view.View.FOCUS_DOWN)
            }
        }
    }

    private fun findResponse(query: String): String {
        val q = query.lowercase()
        return aiResponses.entries.firstOrNull { (key, _) -> q.contains(key) }?.value
            ?: defaultResponse
    }

    private fun startTypingAnimation() {
        val dots = listOf("●  ○  ○", "○  ●  ○", "○  ○  ●", "●  ●  ○", "●  ●  ●")
        lifecycleScope.launch {
            var i = 0
            while (binding.layoutTyping.visibility == android.view.View.VISIBLE) {
                binding.tvTyping.text = dots[i % dots.size]
                i++
                delay(350)
            }
        }
    }

    // ═══════════════════════════════════════
    // UTILS
    // ═══════════════════════════════════════

    private fun animateCounter(from: Int, to: Int, onUpdate: (Int) -> Unit) {
        ValueAnimator.ofInt(from, to).apply {
            duration = 900
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { onUpdate(it.animatedValue as Int) }
            start()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etChatInput.windowToken, 0)
    }
}
