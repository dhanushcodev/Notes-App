package com.minimal.notes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.minimal.notes.R
import com.minimal.notes.databinding.BottomSheetLayoutBinding
import com.minimal.notes.model.Note
import com.minimal.notes.viewmodel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.minimal.notes.utils.DeleteDialog
import com.minimal.notes.utils.ShareNote

class BottomSheetFragment(
    private val parentView: View,
    private val notesViewModel: NoteViewModel,
    private val currentNote: Note,
    private val from: String,
) : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = BottomSheetLayoutBinding.inflate(inflater, container, false)

        binding.deleteNote.setOnClickListener {
            deleteNote()
        }
        binding.shareNote.setOnClickListener {
            shareText()
        }

        return binding.root
    }

    private fun deleteNote() {
        val onDelete = {
            notesViewModel.deleteNote(currentNote)
            if(this.from =="UpdateActivity"){
                parentView.findNavController().navigateUp()
            }
            Snackbar.make(parentView, "Note Deleted", Snackbar.LENGTH_SHORT).show()
        }
        DeleteDialog.showDialog(requireContext(),onDelete,{})
        dismiss()
    }


    private fun shareText() {
        val shareTitle = currentNote.noteTitle
        val shareContent = currentNote.noteContent
        ShareNote.share(requireContext(),shareTitle,shareContent)
        dismiss()
    }

}


