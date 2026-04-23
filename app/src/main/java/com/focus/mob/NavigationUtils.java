package com.focus.mob;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageButton;
import androidx.core.content.ContextCompat;

public class NavigationUtils {

    public enum Tab {
        HOME, INSIGHTS_IA, STATS, SETTINGS
    }

    public static void setupBottomNavigation(Activity activity, Tab activeTab) {
        ImageButton navHome = activity.findViewById(R.id.nav_home);
        ImageButton navInsightsIa = activity.findViewById(R.id.nav_insights_ia);
        ImageButton navStats = activity.findViewById(R.id.nav_stats);
        ImageButton navSettings = activity.findViewById(R.id.nav_settings);

        if (navHome == null || navInsightsIa == null || navStats == null || navSettings == null) return;

        // Reset all colors
        int colorInactive = ContextCompat.getColor(activity, R.color.text_secondary_dark);
        int colorActive = ContextCompat.getColor(activity, R.color.primary);

        navHome.setColorFilter(colorInactive);
        navInsightsIa.setColorFilter(colorInactive);
        navStats.setColorFilter(colorInactive);
        navSettings.setColorFilter(colorInactive);

        // Highlight active tab
        switch (activeTab) {
            case HOME:
                navHome.setColorFilter(colorActive);
                break;
            case INSIGHTS_IA:
                navInsightsIa.setColorFilter(colorActive);
                break;
            case STATS:
                navStats.setColorFilter(colorActive);
                break;
            case SETTINGS:
                navSettings.setColorFilter(colorActive);
                break;
        }

        // Set click listeners (avoid navigating to self)
        navHome.setOnClickListener(v -> navigate(activity, HomeActivity.class, activeTab == Tab.HOME));
        navInsightsIa.setOnClickListener(v -> navigate(activity, InsightsIaActivity.class, activeTab == Tab.INSIGHTS_IA));
        navStats.setOnClickListener(v -> navigate(activity, StatsActivity.class, activeTab == Tab.STATS));
        navSettings.setOnClickListener(v -> navigate(activity, SettingsActivity.class, activeTab == Tab.SETTINGS));
    }

    private static void navigate(Activity currentActivity, Class<?> targetClass, boolean isSelf) {
        if (isSelf) return; // Do nothing if clicking the active tab
        Intent intent = new Intent(currentActivity, targetClass);
        currentActivity.startActivity(intent);
        currentActivity.finish();
        currentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
