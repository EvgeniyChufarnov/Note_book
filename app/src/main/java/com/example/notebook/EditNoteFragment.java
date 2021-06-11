package com.example.notebook;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.notebook.database.Note;

public class EditNoteFragment extends Fragment implements DatePickerFragment.DateReceiver {
    private static final String NOTE_EXTRA_KEY = "note";
    private Note note;

    private TextView title;
    private TextView content;
    private TextView date;

    public static EditNoteFragment getInstance(Note note) {
        EditNoteFragment noteFragment = new EditNoteFragment();
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

        return inflater.inflate(R.layout.fragment_edit_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.et_edit_note_title);
        content = view.findViewById(R.id.et_edit_note_content);
        date = view.findViewById(R.id.tv_edit_note_date);

        title.setText(note.getTitle());
        content.setText(note.getContent());
        date.setText(note.getDate());

        date.setOnClickListener(this::showDatePickerDialog);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.save_note_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_save_note) {
            validateInput();
        }

        return true;
    }

    private void validateInput() {
        String titleInput = title.getText().toString();
        String contentInput = content.getText().toString();

        if (!titleInput.isEmpty() && !contentInput.isEmpty()) {
            note.setTitle(title.getText().toString());
            note.setContent(content.getText().toString());
            note.setDate(date.getText().toString());
            ((Contract) requireActivity()).changeNote(note);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof NewNoteFragment.Contract)) {
            throw new IllegalStateException("Activity must implement EditNoteFragment.Contract");
        }
    }

    public interface Contract {
        void changeNote(Note note);
    }
}
