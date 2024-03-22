package com.example.notes.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.notes.R
import com.example.notes.databinding.BottomSheetLayoutBinding
import com.example.notes.model.Note
import com.example.notes.viewmodel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class BottomSheetFragment(val parentView: View,val notesViewModel: NoteViewModel,val currentNote: Note) : BottomSheetDialogFragment() {
    lateinit var delete: TextView
    lateinit var binding: BottomSheetLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = BottomSheetLayoutBinding.inflate(inflater,container,false)

        binding.deleteNote.setOnClickListener {
            deleteNote()
        }
        binding.shareNote.setOnClickListener {
            shareText()
        }

        return binding.root
    }


    private fun deleteNote(){
        val builder = AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
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
        deleteButton.setOnClickListener{
            notesViewModel.deleteNote(currentNote)
                parentView.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)
                parentView.findNavController().popBackStack(R.id.homeFragment,true)
                Snackbar.make(parentView,"Note Deleted", Snackbar.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dismiss()
    }



    fun shareText() {
        val shareText = currentNote.noteContent
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        val chooserIntent = Intent.createChooser(shareIntent, "Share using...")
        startActivity(chooserIntent)
        dismiss()
    }

}


