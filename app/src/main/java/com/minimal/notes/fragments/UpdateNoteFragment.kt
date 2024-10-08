package com.minimal.notes.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.minimal.notes.R
import com.minimal.notes.databinding.FragmentUpdateNoteBinding
import com.minimal.notes.model.Note
import com.minimal.notes.ui.MainActivity


import com.minimal.notes.viewmodel.NoteViewModel
import me.saket.bettermovementmethod.BetterLinkMovementMethod


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
    var x = MutableLiveData<Int>(0);
    var y = MutableLiveData<Int>(0);

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
            val action = event.action
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                 this.x.value = event.x.toInt()
                  this.y.value = event.y.toInt()
            }
            false
        }

        binding.delete.setOnClickListener {
            showBottomSheet(view, notesViewModel, currentNote)
        }

        binding.done.setOnClickListener {
            updateNote(it)
        }

        Linkify.addLinks(binding.editNoteUpdate, Linkify.ALL)
        binding.editNoteUpdate.movementMethod = BetterLinkMovementMethod.newInstance().apply {
            setOnLinkClickListener { textView, url ->
                // Handle click or return false to let the framework handle this link.
                true
            }
            setOnLinkLongClickListener { textView, url ->
                // Handle long-click or return false to let the framework handle this link.
                (textView as EditText).hideKeyboard()
                showPopupMenu(textView,x.value!!,y.value!!,url)
                true
            }
        }

    }

    fun EditText.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    private fun copyLinkToClipboard(link: String) {
        var url = link
        if (url.contains("mailto:"))
            url = url.replace("mailto:","")
        if(url.contains("tel:"))
            url = url.replace("tel:","")
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied", url)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context,url,Toast.LENGTH_SHORT).show()
    }

    private fun showPopupMenu(view: View, x: Int, y: Int,url: String) {
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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            popupWindow.dismiss()
        }

        popupView.findViewById<View>(R.id.action_copy).setOnClickListener { v: View? ->
            copyLinkToClipboard(url)
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