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
import androidx.fragment.app.Fragment;

import com.example.notebook.data.Note;
import com.example.notebook.utils.Utils;

public class EditNoteFragment extends Fragment {
    private static final String NOTE_EXTRA_KEY = "note";
    private Note note;

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView dateTextView;
    private boolean isDateChanged = false;

    public static EditNoteFragment getInstance(Note note) {
        EditNoteFragment noteFragment = new EditNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTE_EXTRA_KEY, note);
        noteFragment.setArguments(bundle);
        return noteFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(NOTE_EXTRA_KEY)) {
            note = arguments.getParcelable(NOTE_EXTRA_KEY);
        }

        titleTextView = view.findViewById(R.id.et_edit_note_title);
        contentTextView = view.findViewById(R.id.et_edit_note_content);
        dateTextView = view.findViewById(R.id.tv_edit_note_date);

        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());
        dateTextView.setText(Utils.dateLongToString(note.getDate()));

        dateTextView.setOnClickListener(this::showDatePickerDialog);
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
        String titleInput = titleTextView.getText().toString().trim();
        String contentInput = contentTextView.getText().toString().trim();

        if (!titleInput.isEmpty() && !contentInput.isEmpty()) {
            note.setTitle(titleTextView.getText().toString());
            note.setContent(contentTextView.getText().toString());
            if (isDateChanged) {
                note.setDate(Utils.dateStringToLong(dateTextView.getText().toString()));
            }
            ((Contract) requireActivity()).updateNote(note);
        } else {
            Toast.makeText(getContext(), R.string.validate_text_fail, Toast.LENGTH_SHORT).show();
        }
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setDateListener(this::setDate);
        datePickerFragment.show(requireActivity().getSupportFragmentManager(), null);
    }

    public void setDate(long date) {
        this.dateTextView.setText(Utils.dateLongToString(date));
        isDateChanged = true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof NewNoteFragment.Contract)) {
            throw new IllegalStateException("Activity must implement EditNoteFragment.Contract");
        }
    }

    public interface Contract {
        void updateNote(Note note);
    }
}
