package com.example.notebook;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.notebook.viewModels.NewNoteViewModel;

public class NewNoteFragment extends Fragment {
    private static final String IMAGE_FOLDER = "image/*";
    private static final String IMAGE_URI_EXTRA_KEY = "image_uri";
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
    private NewNoteViewModel viewModel;
    private EditText titleView;
    private EditText contentView;
    private ImageView imageContainer;
    private Uri preloadImageUri = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NewNoteViewModel.class);

        titleView = view.findViewById(R.id.et_edit_new_note_title);
        contentView = view.findViewById(R.id.et_edit_new_note_content);
        imageContainer = view.findViewById(R.id.iv_attached_image);

        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGE_URI_EXTRA_KEY)) {
            String uri = savedInstanceState.getString(IMAGE_URI_EXTRA_KEY);

            if (uri != null) {
                showPreview(Uri.parse(uri));
            }
        }
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
        String titleInput = titleView.getText().toString().trim();
        String contentInput = contentView.getText().toString().trim();

        if (!titleInput.isEmpty() && !contentInput.isEmpty()) {
            viewModel.insert(titleInput, contentInput, preloadImageUri, this::onImageUploadFail, requireContext());
            ((Contract) requireActivity()).navigateOutFromNewNote();
        } else {
            Toast.makeText(getContext(), R.string.validate_text_fail, Toast.LENGTH_SHORT).show();
        }
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

    public void onImageUploadFail() {
        Toast.makeText(requireContext(), R.string.cant_load_image, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof NewNoteFragment.Contract)) {
            throw new IllegalStateException("Activity must implement NewNoteFragment.Contract");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (preloadImageUri != null) {
            outState.putString(IMAGE_URI_EXTRA_KEY, preloadImageUri.toString());
        }
    }

    public interface Contract {
        void navigateOutFromNewNote();
    }
}