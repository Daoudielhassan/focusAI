package com.focus.mob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NiveauDistractionActivity extends AppCompatActivity {

    private View[] cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveau_distraction);

        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvPasser = findViewById(R.id.tv_passer);
        Button btnContinuer = findViewById(R.id.btn_continuer);

        cards = new View[]{
                findViewById(R.id.card_zen),
                findViewById(R.id.card_moyen),
                findViewById(R.id.card_distrait)
        };

        // Default selection
        selectCard(findViewById(R.id.card_moyen));

        for (View card : cards) {
            card.setOnClickListener(v -> selectCard(v));
        }

        btnBack.setOnClickListener(v -> finish());
        tvPasser.setOnClickListener(v -> navigateToNext());
        btnContinuer.setOnClickListener(v -> navigateToNext());
    }

    private void selectCard(View selectedCard) {
        for (View card : cards) {
            card.setSelected(card == selectedCard);
        }
    }

    private void navigateToNext() {
        Intent intent = new Intent(NiveauDistractionActivity.this, AutorisationsActivity.class);
        startActivity(intent);
    }
}
