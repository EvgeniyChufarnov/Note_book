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

public class NoteListFragment extends Fragment {
    private static final String NOTES_EXTRA_KEY = "note";
    private NotesListAdapter adapter;
    private List<Note> notes = new ArrayList<>();

    public static NoteListFragment getInstance(ArrayList<Note> notes) {
        NoteListFragment noteListFragment = new NoteListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(NOTES_EXTRA_KEY, notes);
        noteListFragment.setArguments(bundle);
        return noteListFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(NOTES_EXTRA_KEY)) {
            notes = arguments.getParcelableArrayList(NOTES_EXTRA_KEY);
        }

        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_notes_list);
        adapter = new NotesListAdapter(new NotesListAdapter.NoteDiff(), this::onListItemClicked);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(positionStart);
            }
        });
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
