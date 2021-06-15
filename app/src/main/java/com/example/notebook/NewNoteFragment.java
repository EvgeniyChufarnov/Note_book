package com.example.notebook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.notebook.database.Note;

public class NewNoteFragment extends Fragment {
    private final static String IMAGE_FOLDER = "image/*";
    private EditText titleView;
    private EditText contentView;
    private ImageView imageContainer;
    private Uri imageUri = null;

    private final ActivityResultLauncher<String> getPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::setUpPicture
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = view.findViewById(R.id.et_edit_new_note_title);
        contentView = view.findViewById(R.id.et_edit_new_note_content);
        imageContainer = view.findViewById(R.id.iv_attached_image);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.save_and_attach_note_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_save_note) {
            validateInput();
        } else if (item.getItemId() == R.id.menu_item_attach_image) {
            getPictureLauncher.launch(IMAGE_FOLDER);
        }

        return true;
    }

    private void setUpPicture(Uri uri) {
        try {
            Glide.with(this).load(uri).into(imageContainer);
            imageUri = uri;
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.cant_load_image, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void validateInput() {
        String titleInput = titleView.getText().toString();
        String contentInput = contentView.getText().toString();

        if (!titleInput.isEmpty() && !contentInput.isEmpty()) {
            Note note = new Note(
                    titleInput,
                    contentInput,
                    (imageUri != null) ? imageUri.toString() : null
            );
            ((Contract) requireActivity()).addNote(note);
        } else {
            Toast.makeText(getContext(), R.string.validate_text_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof NewNoteFragment.Contract)) {
            throw new IllegalStateException("Activity must implement NewNoteFragment.Contract");
        }
    }

    public interface Contract {
        void addNote(Note note);
    }
}