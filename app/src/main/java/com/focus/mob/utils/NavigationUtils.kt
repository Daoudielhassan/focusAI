package com.focus.mob.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.focus.mob.R
import com.focus.mob.ui.main.HomeActivity
import com.focus.mob.ui.main.InsightsIaActivity
import com.focus.mob.ui.main.SettingsActivity
import com.focus.mob.ui.main.StatsActivity

object NavigationUtils {

    enum class Tab {
        HOME,
        INSIGHTS_IA,
        STATS,
        SETTINGS
    }

    fun setupBottomNavigation(activity: Activity, activeTab: Tab) {
        val colorInactive = ContextCompat.getColor(activity, R.color.text_secondary_dark)
        val colorActive = ContextCompat.getColor(activity, R.color.primary)

        val home = NavItem(
            item = activity.findViewById(R.id.nav_home_item),
            icon = activity.findViewById(R.id.nav_home),
            label = activity.findViewById(R.id.nav_home_label),
            tab = Tab.HOME,
            target = HomeActivity::class.java
        )

        val lumina = NavItem(
            item = activity.findViewById(R.id.nav_lumina_item),
            icon = activity.findViewById(R.id.nav_insights_ia),
            label = activity.findViewById(R.id.nav_lumina_label),
            tab = Tab.INSIGHTS_IA,
            target = InsightsIaActivity::class.java
        )

        val stats = NavItem(
            item = activity.findViewById(R.id.nav_stats_item),
            icon = activity.findViewById(R.id.nav_stats),
            label = activity.findViewById(R.id.nav_stats_label),
            tab = Tab.STATS,
            target = StatsActivity::class.java
        )

        val settings = NavItem(
            item = activity.findViewById(R.id.nav_settings_item),
            icon = activity.findViewById(R.id.nav_settings),
            label = activity.findViewById(R.id.nav_settings_label),
            tab = Tab.SETTINGS,
            target = SettingsActivity::class.java
        )

        val navItems = listOf(home, lumina, stats, settings)

        navItems.forEach { navItem ->
            val isActive = navItem.tab == activeTab

            applyTabState(
                navItem = navItem,
                isActive = isActive,
                activeColor = colorActive,
                inactiveColor = colorInactive
            )

            navItem.item?.setOnClickListener {
                animateClick(navItem.item)
                navigate(
                    currentActivity = activity,
                    targetClass = navItem.target,
                    isSelf = isActive
                )
            }

            navItem.icon?.setOnClickListener {
                animateClick(navItem.icon)
                navigate(
                    currentActivity = activity,
                    targetClass = navItem.target,
                    isSelf = isActive
                )
            }
        }
    }

    private fun applyTabState(
        navItem: NavItem,
        isActive: Boolean,
        activeColor: Int,
        inactiveColor: Int
    ) {
        val color = if (isActive) activeColor else inactiveColor

        navItem.icon?.setColorFilter(color)
        navItem.label?.setTextColor(color)

        navItem.item?.alpha = if (isActive) 1f else 0.72f
        navItem.icon?.scaleX = if (isActive) 1.08f else 1f
        navItem.icon?.scaleY = if (isActive) 1.08f else 1f
    }

    private fun animateClick(view: View?) {
        view ?: return

        view.animate()
            .scaleX(0.94f)
            .scaleY(0.94f)
            .setDuration(90L)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120L)
                    .start()
            }
            .start()
    }

    private fun navigate(
        currentActivity: Activity,
        targetClass: Class<*>,
        isSelf: Boolean
    ) {
        if (isSelf) return

        val intent = Intent(currentActivity, targetClass).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        currentActivity.startActivity(intent)
        currentActivity.fadeTransition()
    }

    private data class NavItem(
        val item: LinearLayout?,
        val icon: ImageButton?,
        val label: TextView?,
        val tab: Tab,
        val target: Class<*>
    )
}