package com.example.notebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.notebook.database.Note;
import com.google.android.material.button.MaterialButton;

public class EditNoteFragment extends Fragment implements DatePickerFragment.DateReceiver {
    private static final String NOTE_KEY = "note";
    private NotesController notesController;
    private Note note;

    private TextView title;
    private TextView content;
    private TextView date;

    public static EditNoteFragment getInstance(Note note) {
        EditNoteFragment noteFragment = new EditNoteFragment();
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

        return inflater.inflate(R.layout.fragment_edit_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.et_edit_note_title);
        content = view.findViewById(R.id.et_edit_note_content);
        date = view.findViewById(R.id.tv_edit_note_date);
        MaterialButton saveButton = view.findViewById(R.id.btn_save_note);

        title.setText(note.getTitle());
        content.setText(note.getContent());
        date.setText(note.getDate());

        saveButton.setOnClickListener(v ->
                validateInput()
        );

        date.setOnClickListener(this::showDatePickerDialog);
    }

    private void validateInput() {
        String titleInput = title.getText().toString();
        String contentInput = content.getText().toString();

        if (!titleInput.isEmpty() && !contentInput.isEmpty()) {
            note.setTitle(title.getText().toString());
            note.setContent(content.getText().toString());
            note.setDate(date.getText().toString());
            notesController.changeNote(note);
        } else {
            Toast.makeText(getContext(), R.string.validate_text_fail, Toast.LENGTH_SHORT).show();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment datePickerFragment = new DatePickerFragment(this);
        datePickerFragment.show(requireActivity().getSupportFragmentManager(), null);
    }

    @Override
    public void setDate(String date) {
        this.date.setText(date);
    }
}
