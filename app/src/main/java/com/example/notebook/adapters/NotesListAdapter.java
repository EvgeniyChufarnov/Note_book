package com.example.notebook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.NotesController;
import com.example.notebook.R;
import com.example.notebook.database.Note;

public class NotesListAdapter extends ListAdapter<Note, NotesListAdapter.NoteViewHolder> {
    private final NotesController notesController;

    public NotesListAdapter(DiffUtil.ItemCallback<Note> diffCallback, NotesController notesController) {
        super(diffCallback);
        this.notesController = notesController;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return NoteViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note current = getItem(position);
        holder.bind(current, notesController);
    }

    protected static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final TextView date;
        private final View view;

        private NoteViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.tv_note_item_title);
            content = view.findViewById(R.id.tv_note_item_content);
            date = view.findViewById(R.id.tv_note_item_date);
            this.view = view;
        }

        static NoteViewHolder create(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notes_list_item, parent, false);
            return new NoteViewHolder(view);
        }

        public void bind(Note note, NotesController notesController) {
            title.setText(note.getTitle());
            content.setText(note.getContent());
            date.setText(note.getDate());
            view.setOnClickListener(v -> notesController.openNote(note));
        }
    }

    public static class NoteDiff extends DiffUtil.ItemCallback<Note> {

        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getContent().equals(newItem.getContent())
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDate().equals(newItem.getDate());
        }
    }
}