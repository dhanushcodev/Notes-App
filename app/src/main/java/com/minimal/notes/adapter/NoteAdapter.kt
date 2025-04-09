package com.minimal.notes.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.minimal.notes.R
import com.minimal.notes.databinding.NoteItemBinding
import com.minimal.notes.model.Note

class NoteAdapter(private val onNoteClickListener: OnNoteClickListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    interface OnNoteClickListener {
        fun onNoteClick(currentNote: Note)
        fun onNoteLongClick(currentNote: Note)
        fun onSelectionModeChange(selectedCount: Int)
    }

    inner class NoteViewHolder(val itemBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private var notesList = emptyList<Note>() // List to hold your notes
    private var selectedNotes = mutableSetOf<Note>()
    private var isSelectionMode = false

    // Add method to get selected notes
    fun getSelectedNotes(): Set<Note> = selectedNotes

    // Add method to clear selection
    fun clearSelection() {
        selectedNotes.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]

        val context = holder.itemView.context

        // Update the visual state based on selection
        if (selectedNotes.contains(currentNote)) {
            holder.itemBinding.noteItem.strokeWidth = 4
//            holder.itemBinding.noteItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.selectedNoteColor))
            holder.itemBinding.noteItemLayout.background = ContextCompat.getDrawable(context, R.color.selectedNoteColor)
        } else {
            holder.itemBinding.noteItem.strokeWidth = 0
//            holder.itemBinding.noteItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.unSelectedNoteColor))
            holder.itemBinding.noteItemLayout.background = ContextCompat.getDrawable(context, R.color.unSelectedNoteColor)

        }

        if (currentNote.noteTitle.isEmpty()) {
            holder.itemBinding.titleContainer.visibility = View.GONE
        } else {
            holder.itemBinding.titleContainer.visibility = View.VISIBLE
        }
        holder.itemBinding.textviewNoteTitle.text = currentNote.noteTitle
//        holder.itemBinding.textviewNoteSubtitle.text = "Tag"
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
            colors.random(), PorterDuff.Mode.SRC_IN
        )

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                toggleNoteSelection(currentNote)
            } else {
                onNoteClickListener.onNoteClick(currentNote)
            }
        }

        holder.itemView.setOnLongClickListener {
            onNoteClickListener.onNoteLongClick(currentNote)
            if (!isSelectionMode) {
                isSelectionMode = true
//                toggleNoteSelection(currentNote)
            }
            true
        }
    }

    fun toggleNoteSelection(note: Note) {
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note)
        } else {
            selectedNotes.add(note)
        }
        
        if (selectedNotes.isEmpty()) {
            isSelectionMode = false
        }
        
        onNoteClickListener.onSelectionModeChange(selectedNotes.size)
        notifyDataSetChanged()
    }

    fun selectAllNotes() {
        selectedNotes.addAll(notesList)
        isSelectionMode = true
        onNoteClickListener.onSelectionModeChange(selectedNotes.size)
        notifyDataSetChanged()
    }

    // Function to update the notes list and refresh the adapter
    fun updateNotes(newNotes: List<Note>) {
        val diffResult = DiffUtil.calculateDiff(NoteDiffCallback(notesList, newNotes))
        notesList = newNotes
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getColor(hex: String): Int {
        return Color.parseColor(hex)
    }

    inner class NoteDiffCallback(
        private val oldList: List<Note>, private val newList: List<Note>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldList[oldItemPosition].id != newList[newItemPosition].id -> {
                    false
                }

                oldList[oldItemPosition].noteTitle != newList[newItemPosition].noteTitle -> {
                    false
                }

                oldList[oldItemPosition].noteContent != newList[newItemPosition].noteContent -> {
                    false
                }

                else -> true
            }
        }
    }

}