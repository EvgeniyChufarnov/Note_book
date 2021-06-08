package com.example.notebook.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    @Ignore
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long id = 0L;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "content")
    public String content;
    @ColumnInfo(name = "date")
    public String date;

    public Note(String title, String content) {
        this.title = title;
        this.content = content;

        Date currentDate = Calendar.getInstance().getTime();
        this.date = dateFormat.format(currentDate);
    }

    protected Note(Parcel in) {
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        date = in.readString();
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
        dest.writeString(date);
    }
}
