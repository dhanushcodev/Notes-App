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
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.AboutActivity
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.SettingsActivity
import com.example.notes.adapter.NoteAdapter
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.model.Note
import com.example.notes.viewmodel.NoteViewModel


class HomeFragment : Fragment(R.layout.fragment_home),
    AdapterView.OnItemClickListener {

    var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var popupWindow: PopupWindow
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var searchActionTriggered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d("homeFragment", "onCreateView: ")
        return binding.root
    }

    val onLongPress: (currentNote: Note) -> Unit = { note ->
        view?.let { it1 -> showBottomSheet(it1, notesViewModel, note) }
    }

    fun showBottomSheet(view: View, noteViewModel: NoteViewModel, currentNote: Note) {
        val bottomSheet = BottomSheetFragment(view, noteViewModel, currentNote, "HomeActivity")
        bottomSheet.show(parentFragmentManager, "MyBottomSheet")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context
        notesViewModel = (activity as MainActivity).noteViewModel
        setUpPopUpMenu()
        setUpSearchView()

        noteAdapter = NoteAdapter(onLongPress)
        sharedPreferences =
            requireContext().getSharedPreferences("layout_preference", Context.MODE_PRIVATE)
                ?: return


        binding.addNote.setOnClickListener {
            it.findNavController().navigate(
                R.id.action_homeFragment_to_newNoteFragment
            )
        }

        setUpRecyclerView()

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

        getNotes()
    }

    private fun setUpPopUpMenu() {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_menu, null)
        popupWindow = PopupWindow(layout, 600, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val menuList = layout.findViewById<ListView>(R.id.menu_list)
        val data = ArrayList<String>()
        data.add("Settings")
        data.add("About")
        val menuAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
        menuList.adapter = menuAdapter
        menuList.onItemClickListener = this
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = true
        binding.mainMenu.setOnClickListener {
            popupWindow.showAsDropDown(it)
        }
    }

    private fun setUpSearchView() {

        val searchView: SearchView = binding.searchBar
        val searchCloseButtonId =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn).id
        val closeButton = searchView.findViewById<ImageView>(searchCloseButtonId)

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (searchActionTriggered && newText != null) {
                    searchNote(newText)
                    Log.d("setUpRecyclerView:searchNote", "called:${newText}")
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchNote(query)
                return false
            }
        })

        closeButton.setOnClickListener {
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchActionTriggered = false
            getNotes()
        }

        binding.searchBar.setOnQueryTextFocusChangeListener { _, hasFocus ->
            searchActionTriggered = hasFocus
        }
    }

    private fun saveLayoutPreference(isStaggered: Boolean) {
        sharedPreferences.edit().putBoolean("layout_preference", isStaggered).apply()
    }

    private fun setUpRecyclerView() {
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
        }

    }

    private fun getNotes(){
        activity?.let {
            notesViewModel.getAllNotes().observe(
                viewLifecycleOwner
            ) {
                Log.d("buggy", "RecyclerView")
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
            Log.d("buggy", "search Note: ${query}")
        }

        Log.d("buggy", "search call")

    }

    fun setSearchResult(list: List<Note>) {
        if (searchActionTriggered)
            noteAdapter.updateNotes(list)
        else {
            binding.searchBar.setQuery("", false)
            binding.searchBar.clearFocus()
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }
        if (position == 1) {
            val intent = Intent(context, AboutActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}