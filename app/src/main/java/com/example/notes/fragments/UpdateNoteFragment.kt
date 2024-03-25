package com.example.notes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.databinding.FragmentUpdateNoteBinding
import com.example.notes.model.Note
import com.example.notes.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar


class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

    lateinit var note: Note
    private lateinit var notesViewModel: NoteViewModel

    lateinit var currentNote: Note
    private val agrs: UpdateNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = agrs.note!!

        binding.editTitleUpdate.setText(currentNote.noteTitle)
        binding.editSubtitleUpdate.text = currentNote.noteSubtitle
        binding.editNoteUpdate.setText(currentNote.noteContent)
        binding.delete.setOnClickListener {
            showBottomSheet(view, notesViewModel, currentNote)
        }

        binding.done.setOnClickListener {
            val title = binding.editTitleUpdate.text.toString().trim()
            val subTitle = binding.editSubtitleUpdate.text.toString().trim()
            val content = binding.editNoteUpdate.text.toString().trim()

            if (content.isNotEmpty()) {
                note = Note(currentNote.id, title, subTitle, content)
                notesViewModel.updateNote(note)
                view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)
                view.findNavController().popBackStack(R.id.homeFragment, true)
                Snackbar.make(it, "Note Updated", Snackbar.LENGTH_SHORT).show()
            } else {

            }
        }

//        val backPressedDispatcher = requireActivity().onBackPressedDispatcher
//        backPressedDispatcher.addCallback(viewLifecycleOwner) {
//
//            view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)
//            view.findNavController().popBackStack(R.id.homeFragment,true)
////            Toast.makeText(context,"backpressed",Toast.LENGTH_SHORT).show()
//        }

    }
    fun showBottomSheet(view: View, noteViewModel: NoteViewModel, currentNote: Note) {
        val bottomSheet = BottomSheetFragment(view, noteViewModel, currentNote)
        bottomSheet.show(parentFragmentManager, "MyBottomSheet")
    }


}