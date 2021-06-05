package com.example.notebook;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.notebook.database.Note;
import com.example.notebook.events.ListUpdateEvent;
import com.example.notebook.viewModels.NotesViewModel;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity implements NotesController {
    private static final int LANDSCAPE_BACKSTACK_LIMIT = 1;
    FragmentManager fragmentManager = getSupportFragmentManager();
    private boolean isLandscape = false;
    private NotesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        viewModel.getAllNotes().observe(this, notes ->
                EventBus.getDefault().post(new ListUpdateEvent(notes))
        );

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
        }

        if (savedInstanceState == null) {
            initNotesList();
        }
    }

    public void initNotesList() {
        NoteListFragment noteListFragment = new NoteListFragment();

        fragmentManager.beginTransaction()
                .add(R.id.list_fragment_container, noteListFragment)
                .commit();
    }

    @Override
    public void addNote(Note note) {
        viewModel.insert(note);
        fragmentManager.popBackStack();
    }

    @Override
    public void changeNote(Note note) {
        viewModel.insert(note);
        fragmentManager.popBackStack();
    }

    @Override
    public void deleteNote(Note note) {
        viewModel.delete(note);
        fragmentManager.popBackStack();
    }

    @Override
    public void openNote(Note note) {
        NoteFragment noteFragment = NoteFragment.getInstance(note);

        if (isLandscape && fragmentManager.getFragments().size() > LANDSCAPE_BACKSTACK_LIMIT) {
            fragmentManager.popBackStack();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, noteFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void openNoteToAdd() {
        NewNoteFragment newNoteFragment = new NewNoteFragment();

        if (isLandscape && fragmentManager.getFragments().size() > LANDSCAPE_BACKSTACK_LIMIT) {
            fragmentManager.popBackStack();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, newNoteFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openNoteToChange(Note note) {
        EditNoteFragment editNoteFragment = EditNoteFragment.getInstance(note);

        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, editNoteFragment)
                .addToBackStack(null)
                .commit();
    }
}