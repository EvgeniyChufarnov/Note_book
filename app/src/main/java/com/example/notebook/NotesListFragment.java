package com.example.notebook;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.adapters.NotesListAdapter;
import com.example.notebook.data.Note;
import com.example.notebook.viewModels.NotesListViewModel;

public class NotesListFragment extends Fragment implements NotesListAdapter.OnItemClicked {
    private NotesListAdapter adapter;
    private NotesListViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NotesListViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.rv_notes_list);
        adapter = new NotesListAdapter(new NotesListAdapter.NoteDiff(), this);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(positionStart);
            }
        });

        viewModel.getNotes().observe(getViewLifecycleOwner(), notes -> adapter.submitList(notes));
    }

    @Override
    public void onItemClick(Note note) {
        ((Contract) requireActivity()).openNote(note);
    }

    @Override
    public void onEditClicked(Note note) {
        ((Contract) requireActivity()).openNoteToChangeFromListFragment(note);
    }

    @Override
    public void onDeleteClicked(Note note) {
        viewModel.delete(note, this::showDeleteFailedMessage, requireContext());
    }

    private void showDeleteFailedMessage() {
        Toast.makeText(requireContext(), R.string.couldnt_delete_note, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.log_out_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_log_out) {
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            intent.putExtra(AuthActivity.IS_LOGGED_OUT, true);
            startActivity(intent);
            viewModel.clearRepository();
            requireActivity().finish();
        }

        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof Contract)) {
            throw new IllegalStateException("Activity must implement NoteListFragment.Contract");
        }
    }

    public interface Contract {
        void openNote(Note note);

        void openNoteToChangeFromListFragment(Note note);
    }
}
