package com.minimal.notes.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.minimal.notes.R
import com.minimal.notes.databinding.BottomSheetLayoutBinding
import com.minimal.notes.model.Note
import com.minimal.notes.viewmodel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

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
        val builder = AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
        val inflater = LayoutInflater.from(activity)
        val customView = inflater.inflate(R.layout.custom_dialog_layout, null)

        builder.setView(customView)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        val dismissButton = customView.findViewById<Button>(R.id.cancel_btn)
        dismissButton.setOnClickListener {
            dialog.dismiss()
        }
        val deleteButton = customView.findViewById<Button>(R.id.delete_btn)
        deleteButton.setOnClickListener {
            notesViewModel.deleteNote(currentNote)
            if(this.from =="UpdateActivity"){
                parentView.findNavController().navigateUp()
            }
            Snackbar.make(parentView, "Note Deleted", Snackbar.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dismiss()
    }


    private fun shareText() {
        val shareTitle = currentNote.noteTitle
        val shareContent = currentNote.noteContent
        val shareText = shareTitle + "\n\n" + shareContent
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        val chooserIntent = Intent.createChooser(shareIntent, "Share using...")
        startActivity(chooserIntent)
        dismiss()
    }

}


