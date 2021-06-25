package com.example.notebook.adapters;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notebook.R;
import com.example.notebook.data.Note;
import com.example.notebook.utils.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NotesListAdapter extends ListAdapter<Note, NotesListAdapter.NoteViewHolder> {
    private static final int NOTE_WITH_IMAGE_TYPE = 0;
    private static final int NOTE_WITHOUT_IMAGE_TYPE = 1;
    private static final String UNKNOWN_TYPE_EXCEPTION_MESSAGE = "Unknown item type";
    private final OnItemClicked clickListener;

    public NotesListAdapter(DiffUtil.ItemCallback<Note> diffCallback, OnItemClicked clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case NOTE_WITH_IMAGE_TYPE:
                return new ImageViewHolder(parent, clickListener);
            case NOTE_WITHOUT_IMAGE_TYPE:
                return new NoImageViewHolder(parent, clickListener);
            default:
                throw new RuntimeException(UNKNOWN_TYPE_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (getItem(position).getImagePath() != null) ? NOTE_WITH_IMAGE_TYPE : NOTE_WITHOUT_IMAGE_TYPE;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note currentNote = getItem(position);
        holder.bind(currentNote);
    }

    public interface OnItemClicked {
        void onItemClick(Note note);

        void onEditClicked(Note note);

        void onDeleteClicked(Note note);
    }

    protected abstract static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final TextView date;
        private final OnItemClicked clickListener;
        private final boolean isLandscape;
        private Note note;

        private NoteViewHolder(ViewGroup parent, OnItemClicked clickListener, int container) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(container, parent, false));

            title = itemView.findViewById(R.id.tv_note_item_title);
            content = itemView.findViewById(R.id.tv_note_item_content);
            date = itemView.findViewById(R.id.tv_note_item_date);
            this.clickListener = clickListener;

            int orientation = parent.getResources().getConfiguration().orientation;
            isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        }

        public void bind(Note note) {
            this.note = note;
            title.setText(note.getTitle());
            content.setText(note.getContent());
            date.setText(Utils.dateLongToString(note.getDate()));

            itemView.setOnClickListener(v -> clickListener.onItemClick(note));

            if (!isLandscape) {
                itemView.setOnLongClickListener(this::initPopupMenu);
            }
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

    private static class NoImageViewHolder extends NoteViewHolder {

        private NoImageViewHolder(ViewGroup parent, OnItemClicked clickListener) {
            super(parent, clickListener, R.layout.notes_list_item);
        }
    }

    private static class ImageViewHolder extends NoteViewHolder {
        private final ImageView noteImageView;

        private ImageViewHolder(ViewGroup parent, OnItemClicked clickListener) {
            super(parent, clickListener, R.layout.notes_list_item_with_image);

            noteImageView = itemView.findViewById(R.id.iv_note_item_image);
        }

        public void bind(Note note) {
            super.bind(note);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference fsReference = storage.getReferenceFromUrl(note.getImagePath());
            Glide.with(itemView.getContext()).load(fsReference).into(noteImageView);
        }
    }

    public static class NoteDiff extends DiffUtil.ItemCallback<Note> {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getContent().equals(newItem.getContent())
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDate() == newItem.getDate();
        }
    }
}