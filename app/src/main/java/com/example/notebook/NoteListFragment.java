package com.example.notebook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.adapters.NotesListAdapter;
import com.example.notebook.database.Note;
import com.example.notebook.events.ListUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteListFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotesListAdapter adapter;
    private List<Note> notes = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_notes_list);
        adapter = new NotesListAdapter(new NotesListAdapter.NoteDiff(), this::onListItemClicked);
        recyclerView.setAdapter(adapter);
        adapter.submitList(notes);

        view.findViewById(R.id.btn_add_new_note).setOnClickListener(v ->
                ((Contract) requireActivity()).openNoteToAdd()
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ListUpdateEvent event) {
        notes = event.notes;
        adapter.submitList(event.notes);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(positionStart);
            }
        });
    }

    private void onListItemClicked(Note note) {
        ((Contract) requireActivity()).openNote(note);
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
        void openNoteToAdd();
    }
}
