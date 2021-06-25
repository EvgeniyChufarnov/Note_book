package com.example.notebook.repository;

import androidx.lifecycle.LiveData;

import com.example.notebook.data.Note;

import java.util.List;

public interface Repository {
    LiveData<List<Note>> getNotes();
    void insert(Note note);
    void delete(Note note);
}
