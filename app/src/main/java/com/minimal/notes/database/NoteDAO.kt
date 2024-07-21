package com.minimal.notes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minimal.notes.model.Note

@Dao
interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes\n" +
            "WHERE \n" +
            "    CASE WHEN :query IS NULL OR TRIM(:query) = '' THEN 1\n" +
            "         ELSE LOWER(noteTitle) LIKE LOWER('%' || :query || '%') OR LOWER(noteContent) LIKE LOWER('%' || :query || '%')\n" +
            "    END\n" +
            "ORDER BY id DESC\n")
    fun searchNote(query: String?): LiveData<List<Note>>
}