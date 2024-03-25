package com.example.notes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.notes.databinding.ActivitySettingsBinding
import com.example.notes.databinding.BottomSheetLayoutBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executor

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = this?.getSharedPreferences("isBiometricEnabled", Context.MODE_PRIVATE) ?: return
        val isBio = sharedPreferences.getBoolean("isBiometricEnabled",false)
        executor = ContextCompat.getMainExecutor(this)
        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

        binding.bioSwitch.isChecked = isBio
        binding.bioSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedPreferences.edit().putBoolean("isBiometricEnabled", true).apply()
            } else {
                if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                    showBiometricPrompt()
                }
            }
        }
    }
    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Secure Login")
            .setSubtitle("Use fingerprint or face to access your notes")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
//            .setNegativeButtonText("Cancel")// Allow fingerprint or face
            .build()

        val biometricPrompt = BiometricPrompt(this, executor, authenticationCallback)
        biometricPrompt.authenticate(promptInfo)
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            // Handle authentication errors (e.g., too many attempts)
            Toast.makeText(applicationContext,"Authentication Failed", Toast.LENGTH_SHORT).show()
            binding.bioSwitch.isChecked = true
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            // User successfully authenticated using biometrics
            // Grant access to your app's functionalities
            sharedPreferences.edit().putBoolean("isBiometricEnabled", false).apply()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            binding.bioSwitch.isChecked = true
            Toast.makeText(applicationContext,"Authentication Failed", Toast.LENGTH_SHORT).show()
            // User failed to authenticate (e.g., wrong fingerprint/face)

        }

    }
}