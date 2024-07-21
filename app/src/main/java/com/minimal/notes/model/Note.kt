package com.minimal.notes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteTitle: String,
    val noteSubtitle: String,
    val noteContent: String
) :Parcelable