package com.example.notebook.viewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.notebook.data.Note;
import com.example.notebook.repository.FirebaseRepository;
import com.example.notebook.repository.FirestoreRepository;
import com.example.notebook.repository.Repository;

public class NotesViewModel extends ViewModel {

    private final Repository repository;
    private final FirestoreRepository imageRepo;

    public NotesViewModel() {
        repository = FirebaseRepository.getInstance();
        imageRepo = FirestoreRepository.getInstance();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void delete(Note note, FirestoreRepository.OnFail errorCallback, Context context) {
        if (note.getImagePath() != null) {
            imageRepo.deleteImage(note, this::deleteFromRepository, errorCallback, context);
        } else {
            deleteFromRepository(note);
        }
    }

    public void deleteFromRepository(Note note) {
        repository.delete(note);
    }
}
