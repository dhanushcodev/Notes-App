package com.minimal.notes.fragments


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.minimal.notes.ui.MainActivity
import com.minimal.notes.R
import com.minimal.notes.ui.SettingsActivity
import com.minimal.notes.adapter.NoteAdapter
import com.minimal.notes.databinding.FragmentHomeBinding
import com.minimal.notes.model.Note
import com.minimal.notes.utils.DeleteDialog
import com.minimal.notes.viewmodel.NoteViewModel


class HomeFragment : Fragment(R.layout.fragment_home), AdapterView.OnItemClickListener {

    var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var popupWindow: PopupWindow
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var menuItems: MutableList<String>
    private var actionMode:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireContext().getSharedPreferences("Note_preference", Context.MODE_PRIVATE)
        return binding.root
    }

    private fun saveLayoutPreference(isStaggered: Boolean) {
        sharedPreferences.edit().putBoolean("isStaggered", isStaggered).apply()
    }

    private fun getLayoutPreference(): Boolean {
        return sharedPreferences.getBoolean("isStaggered", false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context
        notesViewModel = (activity as MainActivity).noteViewModel

        noteAdapter = NoteAdapter(
            object : NoteAdapter.OnNoteClickListener {
                override fun onNoteClick(currentNote: Note) {
                    if (!actionMode) {
                        val direction = HomeFragmentDirections
                            .actionHomeFragmentToUpdateNoteFragment(currentNote)
                        findNavController().navigate(direction)
                    } else {
                        toggleSelection(currentNote)
                    }
                }

                override fun onNoteLongClick(currentNote: Note) {
                    if (!actionMode) {
                        startSelectionMode()
                        toggleSelection(currentNote)
                    }
                }

                override fun onSelectionModeChange(selectedCount: Int) {
                    when (selectedCount) {
                        0 -> {
                            endSelectionMode()
                        }
                        1 -> {
                            if (!actionMode) {
                                startSelectionMode()
                            }
                            binding.selectCount.text= "$selectedCount Note Selected"
                        }
                        else -> {
                            binding.selectCount.text = "$selectedCount Notes Selected"
                        }
                    }
                }
            }
        )

        binding.backButton.setOnClickListener {
            endSelectionMode()
        }

        binding.deleteButton.setOnClickListener {
            deleteSelectedNotes()
        }

        binding.selectAll.setOnClickListener {
            noteAdapter.selectAllNotes()
        }

        binding.addNote.setOnClickListener {
            it.findNavController().navigate(
                R.id.action_homeFragment_to_newNoteFragment
            )
        }

        binding.noteSearch.setOnClickListener {
            it.findNavController().navigate(
                R.id.action_homeFragment_to_searchFragment
            )
        }
        setUpRecyclerView()
        setUpPopUpMenu()
        getNotes()
    }

    private fun setUpPopUpMenu() {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_menu, null)
        popupWindow = PopupWindow(layout, 600, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val menuList = layout.findViewById<ListView>(R.id.menu_list)
        menuItems = mutableListOf("Grid View", "Settings")

        if (getLayoutPreference()) {
            menuItems[0] = "List view"
            Log.d("liststyle", "list view")
        } else {
            Log.d("liststyle", "grid view")
            menuItems[0] = "Grid view"
        }
        val menuAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, menuItems)
        menuList.adapter = menuAdapter
        menuList.onItemClickListener = this
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        binding.mainMenu.setOnClickListener {
            popupWindow.showAsDropDown(it)
        }
    }

    private fun setUpRecyclerView() {
        binding.notesList.apply {
            val value = getLayoutPreference()
            Log.d("liststyle", value.toString() + "setup")
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

    private fun getNotes() {
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

    fun showBottomSheet(view: View, noteViewModel: NoteViewModel, currentNote: Note) {
        val bottomSheet = BottomSheetFragment(view, noteViewModel, currentNote, "HomeActivity")
        bottomSheet.show(parentFragmentManager, "MyBottomSheet")
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(position){
        0 -> {
            if (binding.notesList.layoutManager is StaggeredGridLayoutManager) {
                binding.notesList.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.notesList.adapter = noteAdapter
                menuItems[0] = "Grid View"
            } else {
                binding.notesList.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                binding.notesList.adapter = noteAdapter
                menuItems[0] = "List View"
            }
            saveLayoutPreference(binding.notesList.layoutManager is StaggeredGridLayoutManager)
            (parent?.adapter as ArrayAdapter<*>).notifyDataSetChanged()
            popupWindow.dismiss()
        }
        1 -> {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()

        }
        }
    }

    private fun deleteSelectedNotes() {
        val selectedNotes = noteAdapter.getSelectedNotes()
        DeleteDialog.showDialog(
            requireContext(),
            {
                selectedNotes.forEach { note ->
                    notesViewModel.deleteNote(note)
                }
                Snackbar.make(
                    binding.root,
                    when(selectedNotes.size){
                        1 -> "${selectedNotes.size} Note Deleted"
                        else -> "${selectedNotes.size} Notes Deleted"
                    },
                    Snackbar.LENGTH_SHORT
                ).show()
                endSelectionMode()
            },
            {

            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun startSelectionMode() {
        val params = binding.toolbar.layoutParams as AppBarLayout.LayoutParams
        // Set new scroll flags
        params.scrollFlags = 0

        binding.toolbar.layoutParams = params

        binding.backButton.visibility = View.VISIBLE
        binding.deleteButton.visibility = View.VISIBLE
        binding.selectCount.visibility = View.VISIBLE
        binding.selectAll.visibility = View.VISIBLE
        binding.appTitle.visibility = View.GONE
        binding.noteSearch.visibility = View.GONE
        binding.mainMenu.visibility = View.GONE
        actionMode=true
    }
//
    private fun endSelectionMode(){
    binding.backButton.visibility = View.GONE
    binding.deleteButton.visibility = View.GONE
    binding.selectCount.visibility = View.GONE
    binding.selectAll.visibility = View.GONE
    binding.appTitle.visibility = View.VISIBLE
    binding.noteSearch.visibility = View.VISIBLE
    binding.mainMenu.visibility = View.VISIBLE
    val params = binding.toolbar.layoutParams as AppBarLayout.LayoutParams

    // Set new scroll flags
    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
            AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS

    binding.toolbar.layoutParams = params
    noteAdapter.clearSelection()
    actionMode=false
    }

    private fun toggleSelection(note: Note) {
        noteAdapter.toggleNoteSelection(note)
    }

}