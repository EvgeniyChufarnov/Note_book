package com.example.notebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DeleteNoteBottomSheetFragment extends BottomSheetDialogFragment {
    private OnDeleteConfirmedListener onDeleteConfirmedListener;
    private OnDeleteCanceledListener onDeleteCanceledListener;
    private boolean deleteConfirmed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_fragment_delete_note, container, false);
    }

    public void setOnDeleteConfirmedListener(OnDeleteConfirmedListener onDeleteListener) {
        this.onDeleteConfirmedListener = onDeleteListener;
    }

    public void setOnDeleteCanceledListener(OnDeleteCanceledListener onDeleteCanceled) {
        this.onDeleteCanceledListener = onDeleteCanceled;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(false);

        view.findViewById(R.id.tv_delete_button).setOnClickListener(v -> {
            onDeleteConfirmedListener.onDeleteConfirmed();
            deleteConfirmed = true;
            dismiss();
        });

        view.findViewById(R.id.tv_cancel_button).setOnClickListener(v ->
                dismiss()
        );
    }

    @Override
    public void dismiss() {
        if (!deleteConfirmed) {
            onDeleteCanceledListener.onDeleteCanceled();
        }
        super.dismiss();
    }

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed();
    }

    public interface OnDeleteCanceledListener {
        void onDeleteCanceled();
    }
}
