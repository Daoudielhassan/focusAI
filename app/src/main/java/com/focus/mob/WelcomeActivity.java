package com.focus.mob;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnCommencer = findViewById(R.id.btn_commencer);
        TextView tvLoginPrompt = findViewById(R.id.tv_login_prompt);

        // Highlight "Connexion"
        String text = "Déjà un compte? Connexion";
        SpannableString spannableString = new SpannableString(text);
        int startIndex = text.indexOf("Connexion");
        if (startIndex != -1) {
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white, getTheme())),
                    startIndex, startIndex + "Connexion".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvLoginPrompt.setText(spannableString);

        btnCommencer.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, ObjectifPrincipalActivity.class);
            startActivity(intent);
        });
    }
}
