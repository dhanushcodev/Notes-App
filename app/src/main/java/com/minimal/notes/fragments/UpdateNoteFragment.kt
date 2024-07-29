package com.minimal.notes.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.minimal.notes.ui.MainActivity
import com.minimal.notes.R
import com.minimal.notes.databinding.FragmentUpdateNoteBinding
import com.minimal.notes.model.Note
import com.minimal.notes.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import com.minimal.notes.utils.CustomLinkMovementMethod


class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!
    lateinit var mView: View
    lateinit var note: Note
    private lateinit var notesViewModel: NoteViewModel
    lateinit var currentNote: Note
    private val agrs: UpdateNoteFragmentArgs by navArgs()
    private lateinit var sharedPreferences: SharedPreferences
    private var isAutoSave = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences("Note_preference", Context.MODE_PRIVATE) ?: return
        isAutoSave = sharedPreferences.getBoolean("isAutoSaveEnabled", false)
        mView = view
        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = agrs.note!!

        binding.editTitleUpdate.setText(currentNote.noteTitle)
        binding.editSubtitleUpdate.text = currentNote.noteSubtitle
        binding.editNoteUpdate.setText(currentNote.noteContent)


//        binding.editNoteUpdate.setMovementMethod(CustomLinkMovementMethod(requireContext()));
        val movementMethod = CustomLinkMovementMethod(requireContext())
        binding.editNoteUpdate.movementMethod = movementMethod

        binding.delete.setOnClickListener {
            showBottomSheet(view, notesViewModel, currentNote)
        }

        binding.done.setOnClickListener {
           updateNote(it)
        }

    }
    private fun showBottomSheet(view: View, noteViewModel: NoteViewModel, currentNote: Note) {
        val bottomSheet = BottomSheetFragment(view, noteViewModel, currentNote,"UpdateActivity")
        bottomSheet.show(parentFragmentManager, "MyBottomSheet")
    }

    fun updateNote(view: View) {
        val title = binding.editTitleUpdate.text.toString().trim()
        val subTitle = binding.editSubtitleUpdate.text.toString().trim()
        val content = binding.editNoteUpdate.text.toString().trim()

        if (title != currentNote.noteTitle || content != currentNote.noteContent) {
            note = Note(currentNote.id, title, subTitle, content)
            notesViewModel.updateNote(note)
            view.findNavController().navigateUp()
            Snackbar.make(view, "Note Updated", Snackbar.LENGTH_SHORT).show()
        } else if(content.isEmpty()){
            Snackbar.make(view, "Note is Empty", Snackbar.LENGTH_SHORT).show()
        }else{
            view.findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(isAutoSave){
                    updateNote(mView)
                }else{
                    mView.findNavController().navigateUp()
                }

            }
        })
    }


}