package com.focus.mob;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.focus.mob.data.AppDatabase;
import com.focus.mob.data.SessionRecord;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsActivity extends AppCompatActivity {

    private TextView tvTotalHours, tvTotalMinutes;
    private View[] bars = new View[7];
    private TextView[] tvDays = new TextView[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        tvTotalHours = findViewById(R.id.tv_total_hours);
        tvTotalMinutes = findViewById(R.id.tv_total_minutes);

        bars[0] = findViewById(R.id.bar_1);
        bars[1] = findViewById(R.id.bar_2);
        bars[2] = findViewById(R.id.bar_3);
        bars[3] = findViewById(R.id.bar_4);
        bars[4] = findViewById(R.id.bar_5);
        bars[5] = findViewById(R.id.bar_6);
        bars[6] = findViewById(R.id.bar_7);

        tvDays[0] = findViewById(R.id.tv_day_1);
        tvDays[1] = findViewById(R.id.tv_day_2);
        tvDays[2] = findViewById(R.id.tv_day_3);
        tvDays[3] = findViewById(R.id.tv_day_4);
        tvDays[4] = findViewById(R.id.tv_day_5);
        tvDays[5] = findViewById(R.id.tv_day_6);
        tvDays[6] = findViewById(R.id.tv_day_7);

        NavigationUtils.setupBottomNavigation(this, NavigationUtils.Tab.STATS);
        
        loadData();
    }

    private void loadData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            int totalMinutes = db.sessionDao().getTotalFocusTime();
            List<SessionRecord> sessions = db.sessionDao().getAllSessions();

            runOnUiThread(() -> updateUI(totalMinutes, sessions));
        });
    }

    private void updateUI(int totalMinutes, List<SessionRecord> sessions) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        tvTotalHours.setText(String.valueOf(hours));
        tvTotalMinutes.setText(String.valueOf(minutes));

        // Group sessions by day offset (0 = today, 1 = yesterday, etc., up to 6)
        int[] minutesPerDay = new int[7];
        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);
        long todayStart = currentCal.getTimeInMillis();
        long msPerDay = 24 * 60 * 60 * 1000L;

        for (SessionRecord session : sessions) {
            long diff = todayStart - session.timestamp;
            int daysAgo = diff < 0 ? 0 : (int) (diff / msPerDay) + (session.timestamp < todayStart ? 1 : 0);
            if (daysAgo >= 0 && daysAgo < 7) {
                minutesPerDay[6 - daysAgo] += session.durationMinutes; // 6 is today, 0 is 6 days ago
            }
        }

        // Set labels (Day names)
        String[] dayNames = {"S", "M", "T", "W", "T", "F", "S"};
        int todayDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); // 1=Sun, 2=Mon...
        
        for (int i = 0; i < 7; i++) {
            int dayIndex = (todayDayOfWeek - 1 - (6 - i)) % 7;
            if (dayIndex < 0) dayIndex += 7;
            tvDays[i].setText(dayNames[dayIndex]);
        }

        // Calculate max for chart scaling
        int maxMins = 0;
        for (int m : minutesPerDay) {
            if (m > maxMins) maxMins = m;
        }
        if (maxMins == 0) maxMins = 1; // avoid division by zero

        int maxBarHeightDp = 100;

        for (int i = 0; i < 7; i++) {
            float ratio = (float) minutesPerDay[i] / maxMins;
            int heightDp = (int) (ratio * maxBarHeightDp);
            if (heightDp < 4) heightDp = 4; // minimum height

            int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, getResources().getDisplayMetrics());
            bars[i].getLayoutParams().height = heightPx;
            bars[i].requestLayout();

            // Highlight today (index 6) or the peak day? Let's highlight today.
            if (i == 6 && minutesPerDay[i] > 0) {
                bars[i].setBackgroundResource(R.drawable.bg_chart_bar_active);
                tvDays[i].setTextColor(getResources().getColor(R.color.primary));
            } else {
                bars[i].setBackgroundResource(R.drawable.bg_chart_bar_inactive);
                tvDays[i].setTextColor(getResources().getColor(R.color.text_secondary_dark));
            }
        }
    }
}
