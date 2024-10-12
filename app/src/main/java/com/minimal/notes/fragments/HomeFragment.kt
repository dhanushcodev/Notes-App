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
import com.minimal.notes.ui.AboutActivity
import com.minimal.notes.ui.MainActivity
import com.minimal.notes.R
import com.minimal.notes.ui.SettingsActivity
import com.minimal.notes.adapter.NoteAdapter
import com.minimal.notes.databinding.FragmentHomeBinding
import com.minimal.notes.model.Note
import com.minimal.notes.viewmodel.NoteViewModel


class HomeFragment : Fragment(R.layout.fragment_home), AdapterView.OnItemClickListener {

    var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var popupWindow: PopupWindow
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var menuItems: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireContext().getSharedPreferences("Note_preference", Context.MODE_PRIVATE)
        return binding.root
    }

    private val onLongPress: (currentNote: Note) -> Unit = { note ->
        view?.let { it1 -> showBottomSheet(it1, notesViewModel, note) }
    }

    private val onItemClicked: (currentNote: Note) -> Unit = {
        val direction = HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(it)
        findNavController().navigate(direction)
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

        noteAdapter = NoteAdapter(onLongPress, onItemClicked)

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
        menuItems = mutableListOf("Grid View", "Settings", "About")

        if (getLayoutPreference()) {
            menuItems[0] = "List View"
            Log.d("liststyle", "list view")
        } else {
            Log.d("liststyle", "grid view")
            menuItems[0] = "Grid View"
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
        if (position == 0) {
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
        if (position == 1) {
            val intent = Intent(context, AboutActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }
        if (position == 2) {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}