package com.example.notebook.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.R;
import com.example.notebook.database.Note;

public class NotesListAdapter extends ListAdapter<Note, NotesListAdapter.NoteViewHolder> {
    private final OnItemClicked clickListener;

    public NotesListAdapter(DiffUtil.ItemCallback<Note> diffCallback, OnItemClicked clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(parent, clickListener);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note current = getItem(position);
        holder.bind(current);
    }

    public interface OnItemClicked {
        void onItemClick(Note note);
    }

    protected static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final TextView date;
        private Note note;

        private NoteViewHolder(ViewGroup parent, OnItemClicked clickListener) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notes_list_item, parent, false));
            title = itemView.findViewById(R.id.tv_note_item_title);
            content = itemView.findViewById(R.id.tv_note_item_content);
            date = itemView.findViewById(R.id.tv_note_item_date);
            itemView.setOnClickListener(v -> clickListener.onItemClick(note));
        }

        public void bind(Note note) {
            this.note = note;
            title.setText(note.getTitle());
            content.setText(note.getContent());
            date.setText(note.getDate());
        }
    }

    public static class NoteDiff extends DiffUtil.ItemCallback<Note> {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getContent().equals(newItem.getContent())
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDate().equals(newItem.getDate());
        }
    }
}