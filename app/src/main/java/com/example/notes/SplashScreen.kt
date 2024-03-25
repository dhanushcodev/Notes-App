package com.example.notes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import java.util.concurrent.Executor

class SplashScreen : AppCompatActivity() {
    lateinit var executor: Executor
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences = this?.getSharedPreferences("isBiometricEnabled", Context.MODE_PRIVATE) ?: return
            val isBio = sharedPreferences.getBoolean("isBiometricEnabled",false)
            executor = ContextCompat.getMainExecutor(this)
            val biometricManager = BiometricManager.from(this)
            val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG)

            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                // Device supports biometric authentication (fingerprint or face)
                if (isBio){
                    showBiometricPrompt()
                }
                else{
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                // Handle cases where biometrics aren't supported (e.g., inform user)
            }


    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Secure Login")
            .setSubtitle("Use fingerprint or face to access your notes")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
//            .setNegativeButtonText("Cancel")// Allow fingerprint or face
            .build()

        val biometricPrompt = BiometricPrompt(this, executor, authenticationCallback)
        biometricPrompt.authenticate(promptInfo)
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            // Handle authentication errors (e.g., too many attempts)
            finish()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            // User successfully authenticated using biometrics
            // Grant access to your app's functionalities
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            // User failed to authenticate (e.g., wrong fingerprint/face)

//            finish()
        }

    }
}