package com.example.notebook.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.R;
import com.example.notebook.database.Note;
import com.example.notebook.utils.Utils;

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

        void onEditClicked(Note note);

        void onDeleteClicked(Note note);
    }

    protected static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final TextView date;
        private final OnItemClicked clickListener;
        private Note note;

        private NoteViewHolder(ViewGroup parent, OnItemClicked clickListener) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notes_list_item, parent, false));

            title = itemView.findViewById(R.id.tv_note_item_title);
            content = itemView.findViewById(R.id.tv_note_item_content);
            date = itemView.findViewById(R.id.tv_note_item_date);
            this.clickListener = clickListener;
        }

        public void bind(Note note) {
            this.note = note;
            title.setText(note.getTitle());
            content.setText(note.getContent());
            date.setText(Utils.dateLongToString(note.getDate()));

            itemView.setOnClickListener(v -> clickListener.onItemClick(note));
            itemView.setOnLongClickListener(this::initPopupMenu);
        }

        private boolean initPopupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
            popupMenu.inflate(R.menu.popup_note_menu);
            popupMenu.setOnMenuItemClickListener(this::onPopupMenuClicked);
            popupMenu.show();
            return true;
        }

        private boolean onPopupMenuClicked(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.popup_menu_item_edit) {
                clickListener.onEditClicked(note);
            } else if (menuItem.getItemId() == R.id.popup_menu_item_delete) {
                clickListener.onDeleteClicked(note);
            } else {
                throw new RuntimeException("unknown popup menu item");
            }

            return true;
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
                    && oldItem.getDate() == newItem.getDate();
        }
    }
}