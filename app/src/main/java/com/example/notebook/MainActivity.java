package com.example.notebook;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.notebook.database.Note;
import com.example.notebook.events.ListUpdateEvent;
import com.example.notebook.viewModels.NotesViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteListFragment.Contract,
        NoteFragment.Contract, NewNoteFragment.Contract, EditNoteFragment.Contract {

    private static final int LANDSCAPE_BACKSTACK_LIMIT = 1;
    private static final String STATE_EXTRA_KEY = "State";
    private static final String PORTRAIT_LIST_TAG = "Portrait list";
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private NotesViewModel viewModel;
    private boolean isLandscape = false;
    private boolean isListViewDisplayed = true;
    private boolean needToRestoreList = false;

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

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_EXTRA_KEY)) {
                isListViewDisplayed = savedInstanceState.getBoolean(STATE_EXTRA_KEY);
            }

            removeListNoteFragment();
            restoreState();
        } else {
            initNotesList();
        }
    }

    private void restoreState() {
        if (isLandscape) {
            initNotesList();
        }

        if (isListViewDisplayed) {
            if (!isLandscape) {
                initNotesList();
            }
        } else {
            if (!isLandscape) {
                needToRestoreList = true;
            }
        }
    }

    private void initNotesList() {
        NoteListFragment noteListFragment = new NoteListFragment();
        putNoteListFragmentIntoContainer(noteListFragment);
    }

    private void restoreNotesList() {
        NoteListFragment noteListFragment = NoteListFragment.getInstance(
                (ArrayList<Note>) viewModel.getListOfNodes()
        );

        putNoteListFragmentIntoContainer(noteListFragment);
    }

    private void putNoteListFragmentIntoContainer(NoteListFragment noteListFragment) {
        int containerId = isLandscape ? R.id.list_fragment_container : R.id.main_fragment_container;
        String tag = isLandscape ? null : PORTRAIT_LIST_TAG;

        fragmentManager.beginTransaction()
                .add(containerId, noteListFragment, tag)
                .commit();
    }

    @Override
    public void addNote(Note note) {
        viewModel.insert(note);
        fragmentManager.popBackStack();
        handleFragmentListOnReturn();
        isListViewDisplayed = true;
    }

    @Override
    public void changeNote(Note note) {
        viewModel.insert(note);
        fragmentManager.popBackStack();
        isListViewDisplayed = false;
    }

    @Override
    public void deleteNote(Note note) {
        viewModel.delete(note);
        fragmentManager.popBackStack();
        handleFragmentListOnReturn();
        isListViewDisplayed = true;
    }

    @Override
    public void onBackFromNote() {
        fragmentManager.popBackStack();
        handleFragmentListOnReturn();
        isListViewDisplayed = true;
    }

    @Override
    public void onBackFromNewNote() {
        fragmentManager.popBackStack();
        handleFragmentListOnReturn();
        isListViewDisplayed = true;
    }

    private void handleFragmentListOnReturn() {
        Fragment fragment = fragmentManager.findFragmentByTag(PORTRAIT_LIST_TAG);

        if (!isLandscape) {
            if (fragment == null) {
                restoreNotesList();
            } else if (needToRestoreList) {
                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit();

                restoreNotesList();
                needToRestoreList = false;
            }
        } else {
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
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

        isListViewDisplayed = false;
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

        isListViewDisplayed = false;
    }

    @Override
    public void openNoteToChange(Note note) {
        EditNoteFragment editNoteFragment = EditNoteFragment.getInstance(note);

        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, editNoteFragment)
                .addToBackStack(null)
                .commit();

        isListViewDisplayed = false;
    }

    private void removeListNoteFragment() {
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment instanceof NoteListFragment) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EXTRA_KEY, isListViewDisplayed);
    }
}