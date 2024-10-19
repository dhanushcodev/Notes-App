package com.minimal.notes.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.minimal.notes.R
import com.minimal.notes.databinding.ActivityThemeChangeBinding

class ThemeChangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityThemeChangeBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_theme_change)
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

        setThemeSelected()

        binding.cardSystem.setOnClickListener {
            selectCard(binding.cardSystem)
            sharedPreferences.edit().putInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                .apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        binding.cardLight.setOnClickListener {
            selectCard(binding.cardLight)
            sharedPreferences.edit().putInt("themeMode", AppCompatDelegate.MODE_NIGHT_NO).apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.cardDark.setOnClickListener {
            selectCard(binding.cardDark)
            sharedPreferences.edit().putInt("themeMode", AppCompatDelegate.MODE_NIGHT_YES).apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun selectCard(selectedCard: TextView) {
        resetBorders()
        val drawable = selectedCard.background as LayerDrawable
        val borderDrawable = drawable.getDrawable(0) as GradientDrawable
        borderDrawable.setStroke(8,Color.RED) // Change border to red
    }

    private fun resetBorders() {
        val cards = arrayOf(binding.cardSystem, binding.cardLight, binding.cardDark)
        for (card in cards) {
            val drawable = card.background as LayerDrawable
            val borderDrawable = drawable.getDrawable(0) as GradientDrawable
            val mainDrawable = drawable.getDrawable(1) as GradientDrawable
            when(card){
                binding.cardSystem -> {
                    mainDrawable.setColor(Color.parseColor("#141414"))
                    card.setTextColor(Color.WHITE)
                }
                binding.cardDark -> {
                    mainDrawable.setColor(Color.BLACK)
                    card.setTextColor(Color.WHITE)
                }
                binding.cardLight -> {
                    mainDrawable.setColor(Color.WHITE)
                    card.setTextColor(Color.BLACK)
                }
            }
            borderDrawable.setStroke(4, Color.TRANSPARENT) // Reset border color
        }
    }

    private fun setThemeSelected() {
        val theme =
            sharedPreferences.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
         when (theme) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                selectCard(binding.cardSystem)
            }
            AppCompatDelegate.MODE_NIGHT_NO -> {
                selectCard(binding.cardLight)
            }
            else -> {
                selectCard(binding.cardDark)
            }
        }
    }


}