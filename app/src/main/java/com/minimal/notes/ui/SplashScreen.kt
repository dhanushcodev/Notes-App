package com.minimal.notes.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.minimal.notes.R
import com.minimal.notes.biometric.BiometricPromptManager

class SplashScreen : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        sharedPreferences =
            this.getSharedPreferences("Note_preference", Context.MODE_PRIVATE) ?: return

        val theme =
            sharedPreferences.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(theme)


        val isBio = sharedPreferences.getBoolean("isBiometricEnabled", false)

        val onSuccess = {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val onFail = {
            Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
        }
        val onError = {
            finish()
        }
        var biometricPrompt: BiometricPromptManager? =
            BiometricPromptManager(this, onSuccess, onFail, onError)

        if (isBio) {
            biometricPrompt!!.showPrompt()
        } else {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}