package com.example.notebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.notebook.database.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteFragment extends Fragment {
    private static final String NOTE_KEY = "note";
    private NotesController notesController;
    private Note note;

    private TextView title;
    private TextView content;
    private TextView date;

    public static NoteFragment getInstance(Note note) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTE_KEY, note);
        noteFragment.setArguments(bundle);
        return noteFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(NOTE_KEY)) {
            note = arguments.getParcelable(NOTE_KEY);
        }

        notesController = (NotesController) getActivity();

        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.tv_note_title);
        content = view.findViewById(R.id.tv_note_content);
        date = view.findViewById(R.id.tv_note_date);
        FloatingActionButton editButton = view.findViewById(R.id.btn_edit_note);
        FloatingActionButton deleteButton = view.findViewById(R.id.btn_delete_note);

        setViews();

        editButton.findViewById(R.id.btn_edit_note).setOnClickListener(v ->
                notesController.openNoteToChange(note)
        );

        deleteButton.findViewById(R.id.btn_delete_note).setOnClickListener(v ->
                notesController.deleteNote(note)
        );
    }

    private void setViews() {
        title.setText(note.getTitle());
        content.setText(note.getContent());
        date.setText(note.getDate());
    }
}
