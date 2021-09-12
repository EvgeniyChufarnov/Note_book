package com.example.notebook

import android.content.Context
import com.example.notebook.adapters.NotesListAdapter.OnItemClicked
import com.example.notebook.adapters.NotesListAdapter
import com.example.notebook.viewModels.NotesListViewModel
import android.os.Bundle
import com.example.notebook.R
import com.example.notebook.NotesListFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.notebook.adapters.NotesListAdapter.NoteDiff
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.notebook.DeleteNoteBottomSheetFragment
import com.example.notebook.DeleteNoteBottomSheetFragment.OnDeleteConfirmedListener
import com.example.notebook.DeleteNoteBottomSheetFragment.OnDeleteCanceledListener
import com.example.notebook.repository.FirestoreRepository.OnFail
import android.widget.Toast
import android.content.Intent
import android.content.res.Configuration
import android.view.*
import androidx.fragment.app.Fragment
import com.example.notebook.AuthActivity
import com.example.notebook.data.Note

private const val NOTE_TO_DELETE_KEY = "note to delete key"
private const val DELETE_DIALOG_TAG = "delete dialog"

class NotesListFragment : Fragment(), OnItemClicked {
    private lateinit var adapter: NotesListAdapter
    private lateinit var viewModel: NotesListViewModel
    private var noteToDelete: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setHasOptionsMenu(true)
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(DELETE_DIALOG_TAG)) {
            noteToDelete = savedInstanceState.getParcelable(NOTE_TO_DELETE_KEY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(NotesListViewModel::class.java)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_notes_list)
        adapter = NotesListAdapter(NoteDiff(), this)
        recyclerView.adapter = adapter
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                recyclerView.smoothScrollToPosition(positionStart)
            }
        })
        viewModel.notes.observe(
            viewLifecycleOwner,
            { notes: List<Note?> -> adapter.submitList(notes) })
        if (noteToDelete != null) {
            val onDeleteFragment = requireActivity()
                .supportFragmentManager
                .findFragmentByTag(DELETE_DIALOG_TAG) as DeleteNoteBottomSheetFragment?
            if (onDeleteFragment != null) {
                onDeleteFragment.setOnDeleteConfirmedListener(OnDeleteConfirmedListener { onDeleteConfirmed() })
                onDeleteFragment.setOnDeleteCanceledListener(OnDeleteCanceledListener { onDeleteCanceled() })
            }
        }
    }

    override fun onItemClick(note: Note) {
        (requireActivity() as Contract).openNote(note)
    }

    override fun onEditClicked(note: Note) {
        (requireActivity() as Contract).openNoteToChangeFromListFragment(note)
    }

    override fun onDeleteClicked(note: Note) {
        noteToDelete = note
        val onDeleteFragment = DeleteNoteBottomSheetFragment()
        onDeleteFragment.show(requireActivity().supportFragmentManager, DELETE_DIALOG_TAG)
        onDeleteFragment.setOnDeleteConfirmedListener { onDeleteConfirmed() }
        onDeleteFragment.setOnDeleteCanceledListener { onDeleteCanceled() }
    }

    private fun onDeleteConfirmed() {
        viewModel.delete(noteToDelete, { showDeleteFailedMessage() }, requireContext())
        noteToDelete = null
    }

    private fun onDeleteCanceled() {
        noteToDelete = null
    }

    private fun showDeleteFailedMessage() {
        Toast.makeText(requireContext(), R.string.couldnt_delete_note, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_out_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_log_out) {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.putExtra(AuthActivity.IS_LOGGED_OUT, true)
            startActivity(intent)
            viewModel.clearRepository()
            requireActivity().finish()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (noteToDelete != null) {
            outState.putParcelable(NOTE_TO_DELETE_KEY, noteToDelete)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement NoteListFragment.Contract" }
    }

    interface Contract {
        fun openNote(note: Note?)
        fun openNoteToChangeFromListFragment(note: Note?)
    }
}