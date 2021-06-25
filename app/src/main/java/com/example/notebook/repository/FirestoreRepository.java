package com.example.notebook.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.notebook.data.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class FirestoreRepository {
    public static final String GMAIL_COM = "@gmail.com";
    public static final String PNG = ".png";
    public static String imagesFolder;
    private static volatile FirestoreRepository INSTANCE = null;
    private final FirebaseStorage storage;

    private FirestoreRepository() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        imagesFolder = Objects.requireNonNull(currentUser.getEmail()).replace(GMAIL_COM, "") + "/";
        storage = FirebaseStorage.getInstance();
    }

    public static FirestoreRepository getInstance() {
        synchronized (FirestoreRepository.class) {
            if (INSTANCE == null) {
                INSTANCE = new FirestoreRepository();
            }
            return INSTANCE;
        }
    }

    public void saveImage(Uri uri, OnUploadSuccess onUploadSuccessListener, OnFail onFailListener, Context context) {
        if (isNetworkConnected(context)) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String path = imagesFolder + UUID.randomUUID() + PNG;
            StorageReference storageRef = storage.getReference(path);
            UploadTask uploadTask = storageRef.putFile(uri);

            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imagePath = Objects.requireNonNull(task.getResult()).getStorage().toString();
                    onUploadSuccessListener.onSuccess(imagePath);
                } else {
                    onFailListener.onFail();
                }
            });
        } else {
            onFailListener.onFail();
        }
    }

    public void deleteImage(Note note, OnDeleteSuccess onDeleteSuccessListener, OnFail onFailListener, Context context) {
        if (isNetworkConnected(context)) {
            StorageReference fsReference = storage.getReferenceFromUrl(note.getImagePath());
            fsReference.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    onDeleteSuccessListener.onSuccess(note);
                } else {
                    onFailListener.onFail();
                }
            });
        } else {
            onFailListener.onFail();
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public interface OnUploadSuccess {
        void onSuccess(String path);
    }

    public interface OnDeleteSuccess {
        void onSuccess(Note note);
    }

    public interface OnFail {
        void onFail();
    }
}
