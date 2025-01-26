package com.minimal.notes.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.minimal.notes.R
import com.minimal.notes.adapter.NoteAdapter
import com.minimal.notes.databinding.FragmentSearchBinding
import com.minimal.notes.model.Note
import com.minimal.notes.ui.MainActivity
import com.minimal.notes.viewmodel.NoteViewModel

class SearchFragment : Fragment() {

    var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedPreferences: SharedPreferences


    private fun getLayoutPreference():Boolean{
        return sharedPreferences.getBoolean("isStaggered", false)
    }

    fun showBottomSheet(view: View, noteViewModel: NoteViewModel, currentNote: Note) {
        val bottomSheet = BottomSheetFragment(view, noteViewModel, currentNote, "HomeActivity")
        bottomSheet.show(parentFragmentManager, "MyBottomSheet")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireContext().getSharedPreferences("Note_preference", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context
        notesViewModel = (activity as MainActivity).noteViewModel
        binding.searchBar.requestFocus()

        binding.searchBar.postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }, 200)

        binding.cancel.setOnClickListener{
            it.findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

        noteAdapter = NoteAdapter(object :NoteAdapter.OnNoteClickListener{
            override fun onNoteClick(currentNote: Note) {
                val direction =
                    SearchFragmentDirections.actionSearchFragmentToUpdateNoteFragment(currentNote)
                findNavController().navigate(direction)
            }

            override fun onNoteLongClick(currentNote: Note) {
                view?.let { it1 -> showBottomSheet(it1, notesViewModel, currentNote) }
            }

            override fun onSelectionModeChange(selectedCount: Int) {
                noteAdapter.clearSelection()
            }

        })
        setUpSearchView()
        setUpRecyclerView()
        getNotes()
    }

    private fun setUpSearchView() {

        val searchView: SearchView = binding.searchBar
        val searchCloseButtonId =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn).id
        val closeButton = searchView.findViewById<ImageView>(searchCloseButtonId)

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                    searchNote(newText?.trim())
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchNote(query?.trim())
                return false
            }
        })

        closeButton.setOnClickListener {
            searchView.setQuery("", false)
            searchView.clearFocus()
            getNotes()
        }
    }

    private fun setUpRecyclerView() {
        binding.notesList.apply {
            val value = getLayoutPreference()
            if (value) {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = noteAdapter
            } else {
                layoutManager = LinearLayoutManager(context)
                adapter = noteAdapter
            }
            setHasFixedSize(true)
        }

    }

    private fun getNotes(){
        activity?.let {
            notesViewModel.getAllNotes().observe(
                viewLifecycleOwner
            ) {
                noteAdapter.updateNotes(it)
                if (it.isEmpty()) {
                    updateUIEmpty()
                }
            }
        }
    }

    private fun updateUIEmpty() {
        binding.emptyList.visibility = View.VISIBLE
        binding.notesList.visibility = View.GONE
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        notesViewModel.searchNote(searchQuery).observe(
            viewLifecycleOwner
        ) {
            setSearchResult(it)
        }

    }

    fun setSearchResult(list: List<Note>) {
            noteAdapter.updateNotes(list)
    }


}