package com.example.notebook;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.notebook.database.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class NewNoteFragment extends Fragment {
    private static final String IMAGE_DIR = "imageDir";
    private static final String PNG_FORMAT = ".png";
    private static final  String IMAGE_FOLDER = "image/*";
    private static final int IMAGE_QUALITY = 100;

    private final ActivityResultLauncher<String> getPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::showPreview
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getPictureLauncher.launch(IMAGE_FOLDER);
                } else {
                    Toast.makeText(requireContext(), R.string.unable_to_download, Toast.LENGTH_SHORT).show();
                }
            });

    private EditText titleView;
    private EditText contentView;
    private ImageView imageContainer;
    private String imagePath = null;
    private Uri preloadImageUri = null;


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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
            if (!verifyPermissions()) return true;
            getPictureLauncher.launch(IMAGE_FOLDER);
        }

        return true;
    }

    private void validateInput() {
        String titleInput = titleView.getText().toString();
        String contentInput = contentView.getText().toString();

        if (!titleInput.isEmpty() && !contentInput.isEmpty()) {
            if (preloadImageUri != null) {
                startLoadingPicture();
            } else {
                createNodeAndQuit();
            }
        } else {
            Toast.makeText(getContext(), R.string.validate_text_fail, Toast.LENGTH_SHORT).show();
        }
    }

    private void createNodeAndQuit() {
        String titleInput = titleView.getText().toString();
        String contentInput = contentView.getText().toString();

        Note note = new Note(
                titleInput,
                contentInput,
                (imagePath != null) ? imagePath : null
        );
        ((Contract) requireActivity()).addNote(note);
    }

    private void showPreview(Uri uri) {
        Glide.with(this).load(uri).into(imageContainer);
        preloadImageUri = uri;
    }

    public Boolean verifyPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        return false;
    }

    private void startLoadingPicture() {
        Glide.with(this).load(preloadImageUri).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                saveImage(((BitmapDrawable) resource).getBitmap());
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    private void saveImage(Bitmap image) {
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File directory = contextWrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File imageFile = new File(directory, System.currentTimeMillis() + PNG_FORMAT);

        try (OutputStream outStream = new FileOutputStream(imageFile)) {
            image.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outStream);
            imagePath = imageFile.getAbsolutePath();
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.cant_load_image, Toast.LENGTH_SHORT).show();
        } finally {
            createNodeAndQuit();
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