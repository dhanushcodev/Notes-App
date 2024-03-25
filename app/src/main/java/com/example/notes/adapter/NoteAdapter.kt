package com.example.notes.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.NoteItemBinding
import com.example.notes.fragments.HomeFragmentDirections
import com.example.notes.model.Note

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {


    data class NoteViewHolder(val itemBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    // to calculate updates for recycler view adapter
    private val differCallback = object : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id && oldItem.noteTitle == newItem.noteTitle && oldItem.noteSubtitle == newItem.noteSubtitle && oldItem.noteContent == newItem.noteContent
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }


    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val currentNote = differ.currentList.asReversed()[position]
        if (currentNote.noteTitle.isEmpty()) {
            holder.itemBinding.textviewNoteTitle.visibility = View.GONE
        }
        holder.itemBinding.textviewNoteTitle.text = currentNote.noteTitle
        holder.itemBinding.textviewNoteSubtitle.text = currentNote.noteSubtitle
        holder.itemBinding.textviewNoteContent.text = currentNote.noteContent


        var colors = mutableListOf(
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
            colors[position % colors.size],
            PorterDuff.Mode.SRC_IN
        )

        holder.itemView.setOnClickListener {
            val direction =
                HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }
    }


    fun getColor(hex: String): Int {
        return Color.parseColor(hex)

    }
}