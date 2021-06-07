package com.example.notebook;

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
    private NotesController notesController;
    private NotesListAdapter adapter;
    private List<Note> notes = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notesController = (NotesController) getActivity();
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_notes_list);
        adapter = new NotesListAdapter(new NotesListAdapter.NoteDiff(), notesController);
        recyclerView.setAdapter(adapter);
        adapter.submitList(notes);
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
}
