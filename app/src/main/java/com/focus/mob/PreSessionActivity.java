package com.focus.mob;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PreSessionActivity extends AppCompatActivity {

    private long selectedDurationMs = 30 * 60 * 1000; // Par défaut 30 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_session);

        ImageButton btnBack = findViewById(R.id.btn_back);
        Button btnStartFocus = findViewById(R.id.btn_start_focus);
        TextView tv15 = findViewById(R.id.tv_duration_15);
        TextView tv30 = findViewById(R.id.tv_duration_30);
        TextView tv60 = findViewById(R.id.tv_duration_60);

        // Récupérer la durée de HomeActivity
        int initialDuration = getIntent().getIntExtra("selected_duration", 30);
        updateDurationSelection(initialDuration, tv15, tv30, tv60);

        // Listeners pour changer la durée sur cet écran aussi
        tv15.setOnClickListener(v -> updateDurationSelection(15, tv15, tv30, tv60));
        tv30.setOnClickListener(v -> updateDurationSelection(30, tv15, tv30, tv60));
        tv60.setOnClickListener(v -> updateDurationSelection(60, tv15, tv30, tv60));

        btnBack.setOnClickListener(v -> finish());
        
        btnStartFocus.setOnClickListener(v -> {
            Intent intent = new Intent(PreSessionActivity.this, SessionImmersiveActivity.class);
            intent.putExtra("DURATION_MS", selectedDurationMs);
            startActivity(intent);
        });
    }

    private void updateDurationSelection(int minutes, TextView t15, TextView t30, TextView t60) {
        selectedDurationMs = (long) minutes * 60 * 1000;
        
        // Reset styles
        t15.setBackgroundResource(R.drawable.bg_surface_dark_rounded);
        t15.setTextColor(getResources().getColor(R.color.text_secondary_dark));
        t15.setTypeface(null, android.graphics.Typeface.NORMAL);

        t30.setBackgroundResource(R.drawable.bg_surface_dark_rounded);
        t30.setTextColor(getResources().getColor(R.color.text_secondary_dark));
        t30.setTypeface(null, android.graphics.Typeface.NORMAL);

        t60.setBackgroundResource(R.drawable.bg_surface_dark_rounded);
        t60.setTextColor(getResources().getColor(R.color.text_secondary_dark));
        t60.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Set active style
        TextView selected = (minutes == 15) ? t15 : (minutes == 30) ? t30 : t60;
        selected.setBackgroundResource(R.drawable.bg_duration_chip_active);
        selected.setTextColor(getResources().getColor(R.color.background_dark));
        selected.setTypeface(null, android.graphics.Typeface.BOLD);
    }
}
