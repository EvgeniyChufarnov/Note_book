package com.example.notebook.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notebook.data.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseRepository implements Repository {
    public static String collectionName;
    private static volatile FirebaseRepository INSTANCE = null;
    private final FirebaseFirestore database;
    private final MutableLiveData<List<Note>> notes = new MutableLiveData<>();
    private List<Note> cache = new ArrayList<>();

    private FirebaseRepository() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        collectionName = currentUser.getEmail();

        database = FirebaseFirestore.getInstance();

        database.collection(collectionName).addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;

            cache = new ArrayList<>();
            for (DocumentSnapshot document : value.getDocuments()) {
                cache.add(document.toObject(Note.class));
            }
            sortByDate(cache);
            notes.setValue(cache);
        });
    }

    public static FirebaseRepository getInstance() {
        synchronized (FirebaseRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new FirebaseRepository();
            }
            return INSTANCE;
        }
    }

    public static void clear() {
        INSTANCE = null;
    }

    @Override
    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    @Override
    public void insert(Note note) {
        database.collection(collectionName).document(note.getId()).set(note);
    }

    @Override
    public void delete(Note note) {
        database.collection(collectionName).document(note.getId()).delete();
    }

    private void sortByDate(List<Note> notes) {
        Collections.sort(notes, (first, second) -> Long.compare(second.getDate(), first.getDate()));
    }
}
