package com.example.notebook.database;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "notes_table")
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
    public static final String NO_IMAGE = "no_image";

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long id = 0L;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "content")
    public String content;
    @ColumnInfo(name = "date")
    public long date;
    @ColumnInfo(name = "image_uri")
    public String imageUri;

    public Note(String title, String content, String imageUri) {
        this.title = title;
        this.content = content;
        this.date = Calendar.getInstance().getTimeInMillis();
        this.imageUri = (imageUri != null) ? imageUri : NO_IMAGE;
    }

    protected Note(Parcel in) {
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        date = in.readLong();
    }

    public long getId() {
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

    public Uri getImageUri() {
        return (!imageUri.equals(NO_IMAGE)) ? Uri.parse(imageUri) : null;
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
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(date);
        //dest.writeString(imageUri);
    }
}
