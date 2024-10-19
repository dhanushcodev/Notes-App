package com.minimal.notes.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.minimal.notes.R
import com.minimal.notes.biometric.BiometricPromptManager
import com.minimal.notes.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
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
            this.getSharedPreferences("Note_preference", Context.MODE_PRIVATE) ?: return
        binding.back.setOnClickListener {
            finish()
        }

        binding.autoSaveSwitch.let {
            it.isChecked = sharedPreferences.getBoolean("isAutoSaveEnabled", false)
            it.setOnCheckedChangeListener { _, isChecked ->
                sharedPreferences.edit().putBoolean("isAutoSaveEnabled", isChecked).apply()
            }
        }

        binding.aboutCard.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.themeChangeCard.setOnClickListener {
            startActivity(Intent(this, ThemeChangeActivity::class.java))
        }


        val isBio = sharedPreferences.getBoolean("isBiometricEnabled", false)

        val onSuccess = {
            sharedPreferences.edit().putBoolean("isBiometricEnabled", false).apply()
            binding.bioSwitch.isChecked = false
        }

        val onFail = {
            binding.bioSwitch.isChecked = true
            Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
        }

        val onError = {
            binding.bioSwitch.isChecked = true
        }

        val biometricPromptManager = BiometricPromptManager(this, onSuccess, onFail, onError)
        binding.bioSwitch.isChecked = isBio
        binding.bioSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedPreferences.edit().putBoolean("isBiometricEnabled", true).apply()
            } else {
                if (sharedPreferences.getBoolean(
                        "isBiometricEnabled",
                        false
                    )
                ) biometricPromptManager.showPrompt()
            }
        }
    }



}