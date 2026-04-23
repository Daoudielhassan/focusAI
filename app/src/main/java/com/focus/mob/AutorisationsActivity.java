package com.focus.mob;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AutorisationsActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private static final String PREF_NAME = "LuminaPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorisations);

        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        ImageButton btnBack = findViewById(R.id.btn_back);
        Button btnContinuer = findViewById(R.id.btn_continuer);
        SwitchCompat switchNotif = findViewById(R.id.switch_notif);
        SwitchCompat switchMusic = findViewById(R.id.switch_music);

        // Load saved states
        switchNotif.setChecked(prefs.getBoolean("smart_notifications", true));
        switchMusic.setChecked(prefs.getBoolean("auto_music", false));

        // Listeners for Switches
        switchNotif.setOnCheckedChangeListener((v, isChecked) -> {
            prefs.edit().putBoolean("smart_notifications", isChecked).apply();
        });

        switchMusic.setOnCheckedChangeListener((v, isChecked) -> {
            prefs.edit().putBoolean("auto_music", isChecked).apply();
        });

        btnBack.setOnClickListener(v -> finish());

        // Navigate to Home screen
        btnContinuer.setOnClickListener(v -> {
            Intent intent = new Intent(AutorisationsActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}
