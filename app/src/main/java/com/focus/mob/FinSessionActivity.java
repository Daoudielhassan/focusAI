package com.focus.mob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.focus.mob.data.AppDatabase;
import com.focus.mob.data.SessionRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinSessionActivity extends AppCompatActivity {

    private FrameLayout btnEmoji1, btnEmoji2, btnEmoji3;
    private String selectedMood = "Neutral";
    private long sessionDurationMs = 25 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_session);

        sessionDurationMs = getIntent().getLongExtra("DURATION_MS", 25 * 60 * 1000);

        btnEmoji1 = findViewById(R.id.btn_emoji_1);
        btnEmoji2 = findViewById(R.id.btn_emoji_2);
        btnEmoji3 = findViewById(R.id.btn_emoji_3);

        Button btnInsights = findViewById(R.id.btn_insights);
        Button btnHome = findViewById(R.id.btn_home);

        btnEmoji1.setOnClickListener(v -> selectEmoji(btnEmoji1, "Bad"));
        btnEmoji2.setOnClickListener(v -> selectEmoji(btnEmoji2, "Neutral"));
        btnEmoji3.setOnClickListener(v -> selectEmoji(btnEmoji3, "Great"));

        btnInsights.setOnClickListener(v -> {
            saveSessionToDb();
            Intent intent = new Intent(FinSessionActivity.this, StatsActivity.class);
            startActivity(intent);
        });

        btnHome.setOnClickListener(v -> {
            saveSessionToDb();
            Intent intent = new Intent(FinSessionActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void selectEmoji(FrameLayout btn, String mood) {
        resetEmojis();
        btn.setBackgroundResource(R.drawable.bg_emoji_selected);
        selectedMood = mood;
    }

    private void resetEmojis() {
        btnEmoji1.setBackgroundResource(R.drawable.bg_emoji_unselected);
        btnEmoji2.setBackgroundResource(R.drawable.bg_emoji_unselected);
        btnEmoji3.setBackgroundResource(R.drawable.bg_emoji_unselected);
    }

    private void saveSessionToDb() {
        int minutes = (int) (sessionDurationMs / (1000 * 60));
        
        SessionRecord record = new SessionRecord();
        record.durationMinutes = minutes;
        record.timestamp = System.currentTimeMillis();
        record.moodFeedback = selectedMood;
        record.goal = "Focus Session";
        record.ambiance = "Lumina Default";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            db.sessionDao().insert(record);
        });
    }
}
