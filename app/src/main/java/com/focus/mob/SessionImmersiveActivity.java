package com.focus.mob;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SessionImmersiveActivity extends AppCompatActivity {

    private TextView tvTimer;
    private FrameLayout orbContainer;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 1500000; // 25 minutes par défaut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_immersive);

        tvTimer = findViewById(R.id.tv_timer_val);
        orbContainer = findViewById(R.id.orb_container); // Assurez-vous d'ajouter cet ID dans le XML

        startBreathingAnimation();
        startTimer();
    }

    private void startBreathingAnimation() {
        ScaleAnimation breathe = new ScaleAnimation(
                1.0f, 1.05f, 
                1.0f, 1.05f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        breathe.setDuration(4000);
        breathe.setRepeatMode(Animation.REVERSE);
        breathe.setRepeatCount(Animation.INFINITE);
        
        if (orbContainer != null) {
            orbContainer.startAnimation(breathe);
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                // Logique de fin de session
                finish();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
