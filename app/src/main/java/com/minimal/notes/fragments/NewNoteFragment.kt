package com.minimal.notes.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.minimal.notes.ui.MainActivity
import com.minimal.notes.R
import com.minimal.notes.databinding.FragmentNewNoteBinding
import com.minimal.notes.model.Note
import com.minimal.notes.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date


class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel: NoteViewModel

    private lateinit var mview: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = Date()
        val formatter = SimpleDateFormat("dd MMM yy  h:mm a")
        val formattedString = formatter.format(date)
        notesViewModel = (activity as MainActivity).noteViewModel
        binding.done.setOnClickListener {
            saveNote()
        }
        binding.back.setOnClickListener {
            view.findNavController().navigateUp()
        }
        binding.editSubtitleNew.text = formattedString
        mview = view

    }

    fun saveNote() {
        val noteTitle = binding.editTitleNew.text.toString()
        val noteSubtitle = binding.editSubtitleNew.text.toString()
        val noteContent = binding.editNoteNew.text.toString()

        if (noteContent.isNotEmpty()) {
            val note = Note(0, noteTitle, noteSubtitle, noteContent)
            notesViewModel.addNote(note)
            Snackbar.make(mview, "Note Saved", Snackbar.LENGTH_SHORT).show()
            mview.findNavController().navigateUp()
        } else {
            Snackbar.make(mview, "Note is Empty", Snackbar.LENGTH_SHORT).show()
        }


    }

}