package com.minimal.notes.biometric

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class BiometricPromptManager(
    private val activity: AppCompatActivity,
    val onSucessOperation: () -> Unit,
    val onFailureOperation: () -> Unit,
    val onErrorOperation: () -> Unit
) {
    private val executor = ContextCompat.getMainExecutor(activity)
    fun showPrompt() {
        val biometricManager = BiometricManager.from(activity)
        val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG)

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            showBiometricPrompt()
        } else {
            Toast.makeText(activity, "Biometric Not Supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Secure Login")
            .setSubtitle("Use fingerprint or face to access your notes")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL).build()

        val biometricPrompt = BiometricPrompt(activity, executor, authenticationCallback)
        biometricPrompt.authenticate(promptInfo)
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onErrorOperation()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSucessOperation()

        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onFailureOperation()
        }

    }

}