package com.example.notes.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.NoteItemBinding
import com.example.notes.fragments.HomeFragmentDirections
import com.example.notes.model.Note

class NoteAdapter(val onLongPress: (Note) -> Unit) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val itemBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private var notesList = emptyList<Note>() // List to hold your notes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ))
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList.reversed()[position]

        if (currentNote.noteTitle.isEmpty()) {
            holder.itemBinding.titleContainer.visibility = View.GONE
        }
        holder.itemBinding.textviewNoteTitle.text = currentNote.noteTitle
        holder.itemBinding.textviewNoteSubtitle.text = currentNote.noteSubtitle
        holder.itemBinding.textviewNoteContent.text = currentNote.noteContent

        val colors = listOf(
            getColor("#5d8aa8"),
            getColor("#e52b50"),
            getColor("#efdecd"),
            getColor("#915c83"),
            getColor("#8db600"),
            getColor("#7fffd4"),
            getColor("#e9d66b"),
            getColor("#a1caf1"),
            getColor("#ff9966")
        )

        holder.itemBinding.noteColor.background.setColorFilter(
//            colors[position % colors.size],
            colors.random(),
            PorterDuff.Mode.SRC_IN
        )

        holder.itemView.setOnClickListener {
            val direction =
                HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }

        holder.itemView.setOnLongClickListener {
            onLongPress(currentNote)
            true
        }
    }

    // Function to update the notes list and refresh the adapter
    fun updateNotes(newNotes: List<Note>) {
        notesList = newNotes
        notifyDataSetChanged()
    }

    private fun getColor(hex: String): Int {
        return Color.parseColor(hex)
    }
}