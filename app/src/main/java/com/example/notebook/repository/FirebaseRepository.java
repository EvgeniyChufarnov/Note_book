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
    public static final String ID_KEY = "id";
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
        database.collection(collectionName).add(note);
    }

    @Override
    public void update(Note note) {
        findNote(note, document -> {
            database.collection(collectionName).document(document.getId()).delete();
            database.collection(collectionName).add(note);
        });
    }

    @Override
    public void delete(Note note) {
        findNote(note, document ->
                database.collection(collectionName).document(document.getId()).delete()
        );
    }

    private void findNote(Note note, OnNoteFoundListener noteFoundListener) {
        database.collection(collectionName).whereEqualTo(ID_KEY, note.getId()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        noteFoundListener.onNoteFound(document);
                    }
                });
    }

    private void sortByDate(List<Note> notes) {
        Collections.sort(notes, (first, second) -> Long.compare(second.getDate(), first.getDate()));
    }

    private interface OnNoteFoundListener {
        void onNoteFound(DocumentSnapshot document);
    }
}
