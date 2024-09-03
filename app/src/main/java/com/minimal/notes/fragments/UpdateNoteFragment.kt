package com.minimal.notes.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.style.ClickableSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.minimal.notes.R
import com.minimal.notes.databinding.FragmentUpdateNoteBinding
import com.minimal.notes.model.Note
import com.minimal.notes.ui.MainActivity


import com.minimal.notes.viewmodel.NoteViewModel


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


    @SuppressLint("ClickableViewAccessibility")
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


        binding.editNoteUpdate.setOnTouchListener { v, event ->
            val editText = v as EditText
            val action = event.action
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                val x = event.x.toInt()
                val y = event.y.toInt()

                val layout = editText.layout
                val line = layout.getLineForVertical(y)
                val off = layout.getOffsetForHorizontal(line, x.toFloat())

                val link = editText.text.getSpans(off, off - 1, ClickableSpan::class.java)

                if (link.isNotEmpty()) {
                    if (action == MotionEvent.ACTION_UP) {
                        if (!editText.hasFocus())
                            showPopupMenu(editText, link[0], event.x.toInt(), event.y.toInt())
                        else {
                            val imm =
                                editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

                        }
                    }
                    return@setOnTouchListener true
                } else {
                    Log.d("CustomLinkMovementMethod", "Clicked outside link")
                    editText.requestFocus()
                    val imm =
                        editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                    return@setOnTouchListener false
                }
            }
            false
        }

        binding.delete.setOnClickListener {
            showBottomSheet(view, notesViewModel, currentNote)
        }

        binding.done.setOnClickListener {
            updateNote(it)
        }
    }

    private fun extractTextFromClickableSpan(textView: TextView, clickableSpan: ClickableSpan): String {
        val text = textView.text
        if (text !is Spannable) {
            return ""
        }

        val start = text.getSpanStart(clickableSpan)
        val end = text.getSpanEnd(clickableSpan)

        return text.subSequence(start, end).toString()
    }

    private fun copyLinkToClipboard(link: String) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Link", link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showPopupMenu(view: View, clickableSpan: ClickableSpan, x: Int, y: Int) {
        // Inflate the popup layout
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_menu, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, true
        )

        // Set up the menu item click listeners
        popupView.findViewById<View>(R.id.action_open).setOnClickListener { v: View? ->
            clickableSpan.onClick(view)
            popupWindow.dismiss()
        }

        popupView.findViewById<View>(R.id.action_copy).setOnClickListener { v: View? ->
           val link = extractTextFromClickableSpan(view as TextView,clickableSpan)
            copyLinkToClipboard(link)
            popupWindow.dismiss()
        }

        // Calculate the position
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val screenX = location[0] + x
        val screenY = location[1] + y

        // Show the popup window
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, screenX, screenY - popupWindow.height)

    }

    private fun showBottomSheet(view: View, noteViewModel: NoteViewModel, currentNote: Note) {
        val bottomSheet = BottomSheetFragment(view, noteViewModel, currentNote, "UpdateActivity")
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
        } else if (content.isEmpty()) {
            Snackbar.make(view, "Note is Empty", Snackbar.LENGTH_SHORT).show()
        } else {
            view.findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isAutoSave) {
                        updateNote(mView)
                    } else {
                        mView.findNavController().navigateUp()
                    }

                }
            })
    }


}