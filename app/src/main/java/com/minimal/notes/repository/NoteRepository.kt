package com.minimal.notes.repository

import com.minimal.notes.database.NoteDatabase
import com.minimal.notes.model.Note

class NoteRepository(private val db: NoteDatabase) {

    suspend fun insertNote(note: Note) = db.getNoteDao().insertNote(note)
    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)
    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)
    fun getAllNotes() = db.getNoteDao().getAllNotes()
    fun searchNotes(query: String?) = db.getNoteDao().searchNote(query)
}