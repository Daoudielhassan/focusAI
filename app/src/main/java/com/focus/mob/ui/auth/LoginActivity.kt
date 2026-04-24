package com.focus.mob.ui.auth
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.focus.mob.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    // TODO: Replace with your actual Web Client ID from Firebase Console -> Authentication -> Sign-in method -> Google -> Web SDK configuration
    private val WEB_CLIENT_ID = "949054959351-epoe9pgd0qjguhhup3nsfko5j8an2hip.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.auth_error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setLoading(true)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    setLoading(false)
                    if (task.isSuccessful) {
                        navigateToHome()
                    } else {
                        Toast.makeText(this, getString(R.string.auth_error_firebase, task.exception?.message), Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, getString(R.string.auth_error_reset_empty_email), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, getString(R.string.auth_reset_email_sent), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, getString(R.string.auth_error_firebase, task.exception?.message), Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.tvSignupLink.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun signInWithGoogle() {
        if (WEB_CLIENT_ID == "YOUR_WEB_CLIENT_ID") {
            Toast.makeText(this, getString(R.string.auth_error_google_not_configured), Toast.LENGTH_LONG).show()
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    firebaseAuthWithGoogle(idToken)
                } else {
                    Log.e("Auth", "Unexpected type of credential")
                }
            } catch (e: GetCredentialException) {
                Log.e("Auth", "GetCredentialException", e)
                Toast.makeText(this@LoginActivity, getString(R.string.auth_error_google_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        setLoading(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    navigateToHome()
                } else {
                    Toast.makeText(this, getString(R.string.auth_error_firebase_google, task.exception?.message), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        fadeTransition()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnGoogle.isEnabled = !isLoading
    }
}
