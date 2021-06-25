package com.example.notebook.viewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.notebook.data.Note;
import com.example.notebook.repository.FirebaseRepository;
import com.example.notebook.repository.FirestoreRepository;
import com.example.notebook.repository.Repository;

import java.util.List;

public class NotesListViewModel extends ViewModel {

    private final Repository repository;
    private final LiveData<List<Note>> notes;

    public NotesListViewModel() {
        repository = FirebaseRepository.getInstance();
        notes = repository.getNotes();
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public void delete(Note note, FirestoreRepository.OnFail errorCallback, Context context) {
        if (note.getImagePath() != null) {
            FirestoreRepository ImageRepo = FirestoreRepository.getInstance();
            ImageRepo.deleteImage(note, this::deleteFromRepository, errorCallback, context);
        } else {
            deleteFromRepository(note);
        }
    }

    public void deleteFromRepository(Note note) {
        repository.delete(note);
    }

    public void clearRepository() {
        FirebaseRepository.clear();
    }
}
