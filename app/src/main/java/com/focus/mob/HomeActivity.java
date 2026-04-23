package com.focus.mob;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private int selectedDuration = 30; // Valeur par défaut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnCommencer = findViewById(R.id.btn_commencer);
        TextView tv15 = findViewById(R.id.tv_duration_15);
        TextView tv30 = findViewById(R.id.tv_duration_30);
        TextView tv60 = findViewById(R.id.tv_duration_60);

        // Gestion de la sélection de durée
        tv15.setOnClickListener(v -> updateSelection(15, tv15, tv30, tv60));
        tv30.setOnClickListener(v -> updateSelection(30, tv15, tv30, tv60));
        tv60.setOnClickListener(v -> updateSelection(60, tv15, tv30, tv60));

        btnCommencer.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PreSessionActivity.class);
            intent.putExtra("selected_duration", selectedDuration);
            startActivity(intent);
        });

        NavigationUtils.setupBottomNavigation(this, NavigationUtils.Tab.HOME);
    }

    private void updateSelection(int duration, TextView t15, TextView t30, TextView t60) {
        selectedDuration = duration;
        
        // Reset styles
        t15.setBackgroundResource(android.R.color.transparent);
        t15.setTextColor(getResources().getColor(R.color.text_secondary_dark));
        t30.setBackgroundResource(android.R.color.transparent);
        t30.setTextColor(getResources().getColor(R.color.text_secondary_dark));
        t60.setBackgroundResource(android.R.color.transparent);
        t60.setTextColor(getResources().getColor(R.color.text_secondary_dark));

        // Set active style
        TextView selected = (duration == 15) ? t15 : (duration == 30) ? t30 : t60;
        selected.setBackgroundResource(R.drawable.bg_duration_chip_active);
        selected.setTextColor(getResources().getColor(R.color.background_dark));
    }
}
