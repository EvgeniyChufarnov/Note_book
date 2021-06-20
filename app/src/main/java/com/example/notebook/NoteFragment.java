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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.notebook.data.Note;
import com.example.notebook.utils.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NoteFragment extends Fragment {
    private static final String NOTE_EXTRA_KEY = "note";
    private Note note;

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView dateTextView;
    private ImageView imageContainer;

    public static NoteFragment getInstance(Note note) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NOTE_EXTRA_KEY, note);
        noteFragment.setArguments(bundle);
        return noteFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(NOTE_EXTRA_KEY)) {
            note = arguments.getParcelable(NOTE_EXTRA_KEY);
        }

        titleTextView = view.findViewById(R.id.tv_note_title);
        contentTextView = view.findViewById(R.id.tv_note_content);
        dateTextView = view.findViewById(R.id.tv_note_date);
        imageContainer = view.findViewById(R.id.iv_attached_image);

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
        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());
        dateTextView.setText(Utils.dateLongToString(note.getDate()));

        if ((note.imagePath) != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference fsReference = storage.getReferenceFromUrl(note.imagePath);
            Glide.with(this).load(fsReference).into(imageContainer);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof NotesListFragment.Contract)) {
            throw new IllegalStateException("Activity must implement NoteFragment.Contract");
        }
    }

    public interface Contract {
        void openNoteToChange(Note note);

        void deleteNote(Note note);

        void onBackFromNote();
    }
}
