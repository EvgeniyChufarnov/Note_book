package com.example.notebook.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.notebook.database.Note;
import com.example.notebook.repository.NotesRepository;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private final NotesRepository repository;
    private final LiveData<List<Note>> notes;

    public NotesViewModel(Application application) {
        super(application);
        repository = new NotesRepository(application);
        notes = repository.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }
}
