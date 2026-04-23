package com.focus.mob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ObjectifPrincipalActivity extends AppCompatActivity {

    private View[] cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objectif_principal);

        ImageButton btnBack = findViewById(R.id.btn_back);
        Button btnContinuer = findViewById(R.id.btn_continuer);

        cards = new View[]{
                findViewById(R.id.card_study),
                findViewById(R.id.card_work),
                findViewById(R.id.card_read),
                findViewById(R.id.card_create),
                findViewById(R.id.card_other)
        };

        for (View card : cards) {
            card.setOnClickListener(v -> selectCard(v));
        }

        btnBack.setOnClickListener(v -> finish());

        btnContinuer.setOnClickListener(v -> {
            Intent intent = new Intent(ObjectifPrincipalActivity.this, NiveauDistractionActivity.class);
            startActivity(intent);
        });
    }

    private void selectCard(View selectedCard) {
        for (View card : cards) {
            card.setSelected(card == selectedCard);
        }
    }
}
