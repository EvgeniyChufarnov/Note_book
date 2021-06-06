package com.example.notebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.notebook.database.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewNoteFragment extends Fragment {
    EditText title;
    EditText content;
    private NotesController notesController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notesController = (NotesController) getActivity();

        return inflater.inflate(R.layout.fragment_new_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.et_edit_new_note_title);
        content = view.findViewById(R.id.et_edit_new_note_content);
        FloatingActionButton saveButton = view.findViewById(R.id.btn_save_new_note);

        saveButton.setOnClickListener(v ->
                validateInput()
        );
    }

    private void validateInput() {
        String titleInput = title.getText().toString();
        String contentInput = content.getText().toString();

        if (!titleInput.isEmpty() && !contentInput.isEmpty()) {
            Note note = new Note(titleInput, contentInput);
            notesController.addNote(note);
        } else {
            Toast.makeText(getContext(), R.string.validate_text_fail, Toast.LENGTH_SHORT).show();
        }
    }
}