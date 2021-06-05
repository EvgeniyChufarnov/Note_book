package com.example.notebook.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.notebook.database.Note;
import com.example.notebook.database.NotesDao;
import com.example.notebook.database.NotesDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesRepository {
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private final NotesDao notesDao;
    private final LiveData<List<Note>> allNotes;

    public NotesRepository(Application application) {
        NotesDatabase db = NotesDatabase.getInstance(application);
        notesDao = db.notesDao();
        allNotes = notesDao.getAll();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void insert(Note note) {
        databaseWriteExecutor.execute(() ->
                notesDao.insert(note)
        );
    }

    public void delete(Note note) {
        databaseWriteExecutor.execute(() ->
                notesDao.delete(note)
        );
    }
}
