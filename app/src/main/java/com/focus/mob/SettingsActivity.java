package com.focus.mob;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.focus.mob.data.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchNotifications;
    private SwitchCompat switchMusic;
    private SeekBar seekbarGoal;
    private TextView tvGoalVal;
    private Button btnResetData;
    
    private SharedPreferences prefs;
    private static final String PREF_NAME = "LuminaPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        NavigationUtils.setupBottomNavigation(this, NavigationUtils.Tab.SETTINGS);

        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        switchNotifications = findViewById(R.id.switch_notifications);
        switchMusic = findViewById(R.id.switch_music);
        seekbarGoal = findViewById(R.id.seekbar_goal);
        tvGoalVal = findViewById(R.id.tv_goal_val);
        btnResetData = findViewById(R.id.btn_reset_data);

        // Load Preferences
        switchNotifications.setChecked(prefs.getBoolean("smart_notifications", true));
        switchMusic.setChecked(prefs.getBoolean("auto_music", false));
        int goalProgress = prefs.getInt("daily_goal", 3); // 3 maps to 4 hrs (1 + 3)
        seekbarGoal.setProgress(goalProgress);
        tvGoalVal.setText((goalProgress + 1) + " hrs");

        // Listeners for Switches
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("smart_notifications", isChecked).apply();
        });

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("auto_music", isChecked).apply();
        });

        // Listener for SeekBar
        seekbarGoal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int hours = progress + 1; // 0=1h, 1=2h, ..., 7=8h
                tvGoalVal.setText(hours + " hrs");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.edit().putInt("daily_goal", seekBar.getProgress()).apply();
            }
        });

        // Listener for Reset
        btnResetData.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                db.sessionDao().deleteAllSessions();
                
                runOnUiThread(() -> {
                    Toast.makeText(SettingsActivity.this, "AI Data & History Reset", Toast.LENGTH_SHORT).show();
                });
            });
        });
    }
}
