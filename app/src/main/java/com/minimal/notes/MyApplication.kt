package com.minimal.notes

import android.app.Application
import com.minimal.notes.database.NoteDatabase
import com.minimal.notes.repository.NoteRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
//    private lateinit var noteRepository: NoteRepository
//    lateinit var noteViewModelFactory: NoteViewModelFactory
//
//    override fun onCreate() {
//        super.onCreate()
//        noteRepository = NoteRepository(NoteDatabase(this))
//        noteViewModelFactory = NoteViewModelFactory(this, noteRepository = noteRepository)
//    }
}