package com.example.notebook;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.notebook.database.Note;
import com.example.notebook.events.ListUpdateEvent;
import com.example.notebook.viewModels.NotesViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteListFragment.Contract,
        NoteFragment.Contract, NewNoteFragment.Contract, EditNoteFragment.Contract {

    private static final int LANDSCAPE_BACKSTACK_LIMIT = 1;
    private static final int HIDE_NOTE_FLAG = 0;
    private static final String STATE_EXTRA_KEY = "State";
    private static final String PORTRAIT_LIST_TAG = "Portrait list";
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
        isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;

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
        if (!isLandscape && !isListViewDisplayed) {
            needToRestoreList = true;
        } else {
            initNotesList();
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

        getSupportFragmentManager().beginTransaction()
                .add(containerId, noteListFragment, tag)
                .commit();
    }

    @Override
    public void addNote(Note note) {
        viewModel.insert(note);
        getSupportFragmentManager().popBackStack();
        handleFragmentListOnReturn();
        isListViewDisplayed = true;
        hideKeyboard();
    }

    @Override
    public void changeNote(Note note) {
        viewModel.insert(note);
        getSupportFragmentManager().popBackStack();
        isListViewDisplayed = false;
    }

    @Override
    public void deleteNote(Note note) {
        viewModel.delete(note);
        getSupportFragmentManager().popBackStack();
        handleFragmentListOnReturn();
        isListViewDisplayed = true;
    }

    @Override
    public void onBackFromNote() {
        getSupportFragmentManager().popBackStack();
        handleFragmentListOnReturn();
        isListViewDisplayed = true;
    }

    @Override
    public void onBackFromNewNote() {
        getSupportFragmentManager().popBackStack();
        handleFragmentListOnReturn();
        isListViewDisplayed = true;
    }

    private void handleFragmentListOnReturn() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PORTRAIT_LIST_TAG);

        if (!isLandscape) {
            if (fragment == null) {
                restoreNotesList();
            } else if (needToRestoreList) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();

                restoreNotesList();
                needToRestoreList = false;
            }
        } else {
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    @Override
    public void openNote(Note note) {
        NoteFragment noteFragment = NoteFragment.getInstance(note);

        if (isLandscape && getSupportFragmentManager().getFragments().size() > LANDSCAPE_BACKSTACK_LIMIT) {
            getSupportFragmentManager().popBackStack();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, noteFragment)
                .addToBackStack(null)
                .commit();

        isListViewDisplayed = false;
    }

    @Override
    public void openNoteToAdd() {
        NewNoteFragment newNoteFragment = new NewNoteFragment();

        if (isLandscape && getSupportFragmentManager().getFragments().size() > LANDSCAPE_BACKSTACK_LIMIT) {
            getSupportFragmentManager().popBackStack();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, newNoteFragment)
                .addToBackStack(null)
                .commit();

        isListViewDisplayed = false;
    }

    @Override
    public void openNoteToChange(Note note) {
        EditNoteFragment editNoteFragment = EditNoteFragment.getInstance(note);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, editNoteFragment)
                .addToBackStack(null)
                .commit();

        isListViewDisplayed = false;
    }

    private void removeListNoteFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof NoteListFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EXTRA_KEY, isListViewDisplayed);
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), HIDE_NOTE_FLAG);
    }
}