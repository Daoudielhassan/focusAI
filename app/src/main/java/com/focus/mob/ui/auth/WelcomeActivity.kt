package com.focus.mob.ui.auth
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.focus.mob.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Skip if user is already logged in
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }

        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Highlight "Connexion"
        val text = "Déjà un compte? Connexion"
        val spannable = SpannableString(text)
        val startIndex = text.indexOf("Connexion")
        if (startIndex != -1) {
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)),
                startIndex, startIndex + "Connexion".length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvLoginPrompt.text = spannable

        // Go to Login
        binding.tvLoginPrompt.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            fadeTransition()
        }

        // Go to Sign Up
        binding.btnCommencer.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            fadeTransition()
        }
    }
}
