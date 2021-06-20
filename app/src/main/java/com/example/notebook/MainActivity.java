package com.example.notebook;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.notebook.data.Note;
import com.example.notebook.viewModels.NotesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NotesListFragment.Contract,
        NoteFragment.Contract, NewNoteFragment.Contract, EditNoteFragment.Contract {

    private static final int LANDSCAPE_BACKSTACK_LIMIT = 1;
    private static final int HIDE_NOTE_FLAG = 0;
    private static final String LIST_STATE_EXTRA_KEY = "List state";
    private static final String NAVIGATION_STATE_EXTRA_KEY = "Navigation tate";
    private static final String PORTRAIT_LIST_TAG = "Portrait list";
    private NotesViewModel viewModel;
    private boolean isLandscape = false;
    private boolean isListViewDisplayed = true;
    private boolean isNotesListNavigationActivated = true;
    private boolean needToRestoreList = false;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        int orientation = getResources().getConfiguration().orientation;
        isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::navigate);

        setUpAppBar();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LIST_STATE_EXTRA_KEY)) {
                isListViewDisplayed = savedInstanceState.getBoolean(LIST_STATE_EXTRA_KEY);
            }

            if (savedInstanceState.containsKey(NAVIGATION_STATE_EXTRA_KEY)) {
                isNotesListNavigationActivated = savedInstanceState.getBoolean(NAVIGATION_STATE_EXTRA_KEY);
            }

            removeListNoteFragment();

            if (isNotesListNavigationActivated) {
                restoreState();
            }
        } else {
            initNotesList();
        }
    }

    private void setUpAppBar() {
        ActionBar appbar = getSupportActionBar();
        assert appbar != null;
        appbar.setTitle(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
    }

    private boolean navigate(MenuItem item) {
        if (item.getItemId() == R.id.notes_bottom_navigation_item) {
            navigateToNotesList();
        } else if (item.getItemId() == R.id.new_note_bottom_navigation_item) {
            navigateToNewNote();
        } else if (item.getItemId() == R.id.about_app_bottom_navigation_item) {
            navigateToAboutApp();
        }
        return true;
    }

    private void navigateToNotesList() {
        removeAllFragments();
        initNotesList();
        isListViewDisplayed = true;
        isNotesListNavigationActivated = true;
    }

    private void navigateToNewNote() {
        navigateToFragment(new NewNoteFragment());
    }

    private void navigateToAboutApp() {
        navigateToFragment(new AboutAppFragment());
    }

    private void navigateToFragment(Fragment fragment) {
        removeAllFragments();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.full_width_container, fragment)
                .commit();

        isListViewDisplayed = false;
        isNotesListNavigationActivated = false;
    }

    private void restoreState() {
        if (!isLandscape && !isListViewDisplayed) {
            needToRestoreList = true;
        } else {
            initNotesList();
        }
    }

    private void initNotesList() {
        NotesListFragment notesListFragment = new NotesListFragment();

        int containerId = isLandscape ? R.id.list_fragment_container : R.id.main_fragment_container;
        String tag = isLandscape ? null : PORTRAIT_LIST_TAG;

        getSupportFragmentManager().beginTransaction()
                .add(containerId, notesListFragment, tag)
                .commit();
    }

    @Override
    public void navigateOutFromNewNote() {
        hideKeyboard();
        bottomNavigationView.setSelectedItemId(R.id.notes_bottom_navigation_item);
    }

    @Override
    public void updateNote(Note note) {
        viewModel.update(note);
        getSupportFragmentManager().popBackStack();
        isListViewDisplayed = false;
    }

    @Override
    public void deleteNote(Note note) {
        viewModel.delete(note, this::showDeleteFailedMessage, this);
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

    private void handleFragmentListOnReturn() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PORTRAIT_LIST_TAG);

        if (!isLandscape) {
            if (fragment == null) {
                initNotesList();
            } else if (needToRestoreList) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();

                initNotesList();
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
    public void openNoteToChange(Note note) {
        EditNoteFragment editNoteFragment = EditNoteFragment.getInstance(note);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, editNoteFragment)
                .addToBackStack(null)
                .commit();

        isListViewDisplayed = false;
    }

    @Override
    public void openNoteToChangeFromListFragment(Note note) {
        openNote(note);
        openNoteToChange(note);
    }

    private void removeAllFragments() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void removeListNoteFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof NotesListFragment) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LIST_STATE_EXTRA_KEY, isListViewDisplayed);
        outState.putBoolean(NAVIGATION_STATE_EXTRA_KEY, isNotesListNavigationActivated);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), HIDE_NOTE_FLAG);
    }

    private void showDeleteFailedMessage() {
        Toast.makeText(this, R.string.couldnt_delete_note, Toast.LENGTH_SHORT).show();
    }
}