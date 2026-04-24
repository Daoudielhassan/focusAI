package com.focus.mob.utils
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat

object NavigationUtils {

    enum class Tab { HOME, INSIGHTS_IA, STATS, SETTINGS }

    fun setupBottomNavigation(activity: Activity, activeTab: Tab) {
        val navHome = activity.findViewById<android.widget.ImageButton>(R.id.nav_home) ?: return
        val navInsightsIa = activity.findViewById<android.widget.ImageButton>(R.id.nav_insights_ia) ?: return
        val navStats = activity.findViewById<android.widget.ImageButton>(R.id.nav_stats) ?: return
        val navSettings = activity.findViewById<android.widget.ImageButton>(R.id.nav_settings) ?: return

        val colorInactive = ContextCompat.getColor(activity, R.color.text_secondary_dark)
        val colorActive = ContextCompat.getColor(activity, R.color.primary)

        listOf(navHome, navInsightsIa, navStats, navSettings).forEach { it.setColorFilter(colorInactive) }

        when (activeTab) {
            Tab.HOME -> navHome.setColorFilter(colorActive)
            Tab.INSIGHTS_IA -> navInsightsIa.setColorFilter(colorActive)
            Tab.STATS -> navStats.setColorFilter(colorActive)
            Tab.SETTINGS -> navSettings.setColorFilter(colorActive)
        }

        navHome.setOnClickListener { navigate(activity, HomeActivity::class.java, activeTab == Tab.HOME) }
        navInsightsIa.setOnClickListener { navigate(activity, InsightsIaActivity::class.java, activeTab == Tab.INSIGHTS_IA) }
        navStats.setOnClickListener { navigate(activity, StatsActivity::class.java, activeTab == Tab.STATS) }
        navSettings.setOnClickListener { navigate(activity, SettingsActivity::class.java, activeTab == Tab.SETTINGS) }
    }

    private fun navigate(currentActivity: Activity, targetClass: Class<*>, isSelf: Boolean) {
        if (isSelf) return
        currentActivity.startActivity(Intent(currentActivity, targetClass))
        currentActivity.finish()
        currentActivity.fadeTransition()
    }
}
