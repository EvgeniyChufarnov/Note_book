package com.example.notebook;

import com.example.notebook.database.Note;

public interface NotesController {
    void addNote(Note note);

    void changeNote(Note note);

    void deleteNote(Note note);

    void openNote(Note note);

    void openNoteToAdd();

    void openNoteToChange(Note note);
}
