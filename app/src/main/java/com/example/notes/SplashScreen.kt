package com.example.notes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.notes.biometric.BiometricPromptManager
import java.util.concurrent.Executor

class SplashScreen : AppCompatActivity() {
    lateinit var executor: Executor
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        sharedPreferences =
            this.getSharedPreferences("isBiometricEnabled", Context.MODE_PRIVATE) ?: return
        val isBio = sharedPreferences.getBoolean("isBiometricEnabled", false)
        executor = ContextCompat.getMainExecutor(this)

        val onSucess = {
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
            BiometricPromptManager(this, executor, onSucess, onFail, onError)

        if (isBio) {
            biometricPrompt!!.showPrompt()
            biometricPrompt = null
        } else {
            biometricPrompt = null
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}