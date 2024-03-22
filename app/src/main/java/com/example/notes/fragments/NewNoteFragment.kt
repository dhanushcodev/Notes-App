package com.example.notes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.notes.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import com.example.notes.R
import com.example.notes.adapter.NoteAdapter
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.databinding.FragmentNewNoteBinding
import com.example.notes.model.Note
import com.example.notes.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar


class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    var _binding : FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel : NoteViewModel

    private lateinit var mview: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewNoteBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = Date()
        val formatter = SimpleDateFormat("dd MMM yy  h:mm a")
        val formattedString = formatter.format(date)
        notesViewModel = (activity as MainActivity).noteViewModel
        binding.done.setOnClickListener{
            saveNote()
        }
        binding.back.setOnClickListener{
            view.findNavController().popBackStack()
        }
        binding.editSubtitleNew.text = formattedString
        mview = view
//        val backPressedDispatcher = requireActivity().onBackPressedDispatcher
//        backPressedDispatcher.addCallback(viewLifecycleOwner) {
//
//            view.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)
//            view.findNavController().popBackStack(R.id.homeFragment,true)
////            Toast.makeText(context,"backpressed",Toast.LENGTH_SHORT).show()
//        }
    }

    fun saveNote(){
        val noteTitle = binding.editTitleNew.text.toString()
        val noteSubtitle = binding.editSubtitleNew.text.toString()
        val noteContent = binding.editNoteNew.text.toString()

        if(noteContent.isNotEmpty()){
            val note = Note(0,noteTitle,noteSubtitle,noteContent)
            notesViewModel.addNote(note)
//            Toast.makeText(mview.context,"Note Saved",Toast.LENGTH_SHORT).show()
            Snackbar.make(mview,"Note Saved",Snackbar.LENGTH_SHORT).show()
            mview.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)
            mview.findNavController().popBackStack(R.id.homeFragment,true)
        }
        else{
                Toast.makeText(mview.context,"Note is Empty",Toast.LENGTH_SHORT).show()
//            Snackbar.make(mview,"Note is Empty",Snackbar.LENGTH_SHORT).show()
        }


    }

}