package com.example.notebook;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.notebook.database.Note;

public class NoteFragment extends Fragment {
    private static final String NOTE_EXTRA_KEY = "note";
    private Note note;

    private TextView title;
    private TextView content;
    private TextView date;

    public static NoteFragment getInstance(Note note) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTE_EXTRA_KEY, note);
        noteFragment.setArguments(bundle);
        return noteFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(NOTE_EXTRA_KEY)) {
            note = arguments.getParcelable(NOTE_EXTRA_KEY);
        }

        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.tv_note_title);
        content = view.findViewById(R.id.tv_note_content);
        date = view.findViewById(R.id.tv_note_date);

        setViews();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                ((Contract) requireActivity()).onBackFromNote();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_edit_note) {
            ((Contract) requireActivity()).openNoteToChange(note);
        } else if (item.getItemId() == R.id.menu_item_delete_note) {
            ((Contract) requireActivity()).deleteNote(note);
        }

        return true;
    }

    private void setViews() {
        title.setText(note.getTitle());
        content.setText(note.getContent());
        date.setText(note.getDate());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof NoteListFragment.Contract)) {
            throw new IllegalStateException("Activity must implement NoteFragment.Contract");
        }
    }

    public interface Contract {
        void openNoteToChange(Note note);

        void deleteNote(Note note);

        void onBackFromNote();
    }
}
