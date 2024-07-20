package com.example.notes

import android.app.Application
import com.example.notes.database.NoteDatabase
import com.example.notes.repository.NoteRepository
import com.example.notes.viewmodel.NoteViewModelFactory

class MyApplication: Application() {
    private lateinit var noteRepository: NoteRepository
    lateinit var noteViewModelFactory: NoteViewModelFactory

    override fun onCreate() {
        super.onCreate()
        noteRepository = NoteRepository(NoteDatabase(this))
        noteViewModelFactory = NoteViewModelFactory(this, noteRepository = noteRepository)
    }
}