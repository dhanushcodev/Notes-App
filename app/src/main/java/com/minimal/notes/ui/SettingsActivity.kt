package com.minimal.notes.ui

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.minimal.notes.R
import com.minimal.notes.biometric.BiometricPromptManager
import com.minimal.notes.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sharedPreferences =
            this.getSharedPreferences("Note_preference", Context.MODE_PRIVATE) ?: return
        binding.back.setOnClickListener {
            finish()
        }

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.theme_array, android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.themeSpinner.adapter = adapter
        binding.themeSpinner.setSelection(getThemeSelected())
        binding.themeSpinner.onItemSelectedListener = this


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


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position) as String
        if (selectedItem.equals("system", true)) {
            sharedPreferences.edit().putInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                .apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else if (selectedItem.equals("light", true)) {
            sharedPreferences.edit().putInt("themeMode", AppCompatDelegate.MODE_NIGHT_NO).apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            sharedPreferences.edit().putInt("themeMode", AppCompatDelegate.MODE_NIGHT_YES).apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun getThemeSelected(): Int {
        val theme =
            sharedPreferences.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        return when (theme) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                0
            }
            AppCompatDelegate.MODE_NIGHT_NO -> {
                1
            }
            else -> {
                2
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}