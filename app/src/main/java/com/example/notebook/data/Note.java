package com.example.notebook.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.UUID;

public class Note implements Parcelable {
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private String id;
    private String title;
    private String content;
    private long date;
    private String imagePath;

    public Note() {
    }

    public Note(@NonNull String title, @NonNull String content, @Nullable String imagePath) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.date = Calendar.getInstance().getTimeInMillis();
        this.imagePath = imagePath;
    }

    protected Note(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        date = in.readLong();
        imagePath = in.readString();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(date);
        dest.writeString(imagePath);
    }
}
