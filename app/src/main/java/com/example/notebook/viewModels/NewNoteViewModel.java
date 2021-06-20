package com.example.notebook.viewModels;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.example.notebook.data.Note;
import com.example.notebook.repository.FirebaseRepository;
import com.example.notebook.repository.FirestoreRepository;
import com.example.notebook.repository.Repository;

public class NewNoteViewModel extends ViewModel {
    private final Repository repository;
    private final FirestoreRepository firestoreRepository;
    private String currentTitle;
    private String currentContent;
    private OnImageUploadFail listener;

    public NewNoteViewModel() {
        repository = FirebaseRepository.getInstance();
        firestoreRepository = FirestoreRepository.getInstance();
    }

    public void insert(String title, String content, Uri uri, OnImageUploadFail listener, Context context) {
        if (uri != null) {
            currentTitle = title;
            currentContent = content;
            this.listener = listener;

            firestoreRepository.saveImage(uri, this::insertWithImage, this::insertTextOnly, context);
        } else {
            repository.insert(new Note(title, content, null));
        }
    }

    private void insertWithImage(String path) {
        repository.insert(new Note(currentTitle, currentContent, path));
        listener = null;
    }

    private void insertTextOnly() {
        repository.insert(new Note(currentTitle, currentContent, null));
        listener.onImageUploadFail();
        listener = null;
    }

    public interface OnImageUploadFail {
        void onImageUploadFail();
    }
}
