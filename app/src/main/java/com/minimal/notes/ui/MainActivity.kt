package com.minimal.notes.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.minimal.notes.MyApplication
import com.minimal.notes.databinding.ActivityMainBinding
import com.minimal.notes.viewmodel.NoteViewModel

class MainActivity : AppCompatActivity() {
    lateinit var noteViewModel: NoteViewModel
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setUpViewModel()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    private fun setUpViewModel() {
        val app = application as MyApplication
        noteViewModel = ViewModelProvider(
            this, app.noteViewModelFactory
        ).get(NoteViewModel::class.java)

    }

}