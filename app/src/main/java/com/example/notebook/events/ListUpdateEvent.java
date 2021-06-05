package com.example.notebook.events;

import com.example.notebook.database.Note;

import java.util.List;

public class ListUpdateEvent {
    public final List<Note> notes;

    public ListUpdateEvent(List<Note> notes) {
        this.notes = notes;
    }
}
