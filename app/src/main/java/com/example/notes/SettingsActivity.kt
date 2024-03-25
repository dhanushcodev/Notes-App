package com.example.notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.notes.biometric.BiometricPromptManager
import com.example.notes.databinding.ActivitySettingsBinding
import java.util.concurrent.Executor

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences =
            this.getSharedPreferences("isBiometricEnabled", Context.MODE_PRIVATE) ?: return
        val isBio = sharedPreferences.getBoolean("isBiometricEnabled", false)
        executor = ContextCompat.getMainExecutor(this)

        val onSuccess = {
            sharedPreferences.edit().putBoolean("isBiometricEnabled", false).apply()
        }

        val onFail = {
            binding.bioSwitch.isChecked = true
            Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
        }

        val onError = {
            binding.bioSwitch.isChecked = true
        }

        var biometricPromptManager: BiometricPromptManager? =
            BiometricPromptManager(this, executor, onSuccess, onFail, onError)
        binding.bioSwitch.isChecked = isBio
        binding.bioSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedPreferences.edit().putBoolean("isBiometricEnabled", true).apply()
                biometricPromptManager = null
            } else {
                biometricPromptManager!!.showPrompt()
            }
        }
    }

}