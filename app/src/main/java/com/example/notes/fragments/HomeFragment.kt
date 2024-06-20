package com.example.notes.fragments

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
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.SettingsActivity
import com.example.notes.adapter.NoteAdapter
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.viewmodel.NoteViewModel
import kotlin.math.log


class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener,
    AdapterView.OnItemClickListener {

    var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var popupWindow: PopupWindow
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_menu, null)
        popupWindow = PopupWindow(layout, 600, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val menuList = layout.findViewById<ListView>(R.id.menu_list)
        val data = ArrayList<String>()
        data.add("Settings")
//        data.add("About")
        val context = context
        val menuAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, data)
        menuList.adapter = menuAdapter
        menuList.onItemClickListener = this
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        binding.mainMenu.setOnClickListener {
            popupWindow.showAsDropDown(it)
        }
        notesViewModel = (activity as MainActivity).noteViewModel
        binding.searchBar.setOnQueryTextListener(this)
        noteAdapter = NoteAdapter()
        sharedPreferences =
            context.getSharedPreferences("layout_preference", Context.MODE_PRIVATE) ?: return

        binding.addNote.setOnClickListener {
            it.findNavController().navigate(
                R.id.action_homeFragment_to_newNoteFragment
            )
        }
        binding.notesList.apply {
            var value = sharedPreferences.getBoolean("layout_preference", false)
            Log.d("listStyle", value.toString())
            if (!value) {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                binding.listStyle.setImageResource(R.drawable.ic_list_black)
                adapter = noteAdapter
            } else {
                layoutManager = LinearLayoutManager(context)
                binding.listStyle.setImageResource(R.drawable.ic_grid_black)
                adapter = noteAdapter
            }
            setHasFixedSize(true)
//            adapter = noteAdapter

        }
        binding.listStyle.setOnClickListener {
            if (binding.notesList.layoutManager is StaggeredGridLayoutManager) {
                binding.listStyle.setImageResource(R.drawable.ic_grid_black)
                binding.notesList.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.notesList.adapter = noteAdapter
            } else {
                binding.notesList.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                binding.listStyle.setImageResource(R.drawable.ic_list_black)
                binding.notesList.adapter = noteAdapter
            }

            saveLayoutPreference(binding.notesList.layoutManager is LinearLayoutManager)
        }
        setUpRecyclerView()
    }


    private fun saveLayoutPreference(isStaggered: Boolean) {
        sharedPreferences.edit().putBoolean("layout_preference", isStaggered).apply()
    }

    private fun setUpRecyclerView() {

        activity?.let {
            notesViewModel.getAllNotes().observe(
                viewLifecycleOwner
            ) {
                noteAdapter.differ.submitList(it)
                if (it.isEmpty()) {
                    updateUI()
                }

            }
        }
    }

    private fun updateUI() {
        binding.emptyList.visibility = View.VISIBLE
        binding.notesList.visibility = View.GONE
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        searchNote(query)
        return false
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        notesViewModel.searchNote(searchQuery).observe(
            this
        ) { list ->
            noteAdapter.differ.submitList(list)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
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






