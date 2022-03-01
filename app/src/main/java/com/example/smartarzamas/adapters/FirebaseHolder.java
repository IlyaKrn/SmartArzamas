package com.example.smartarzamas.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class FirebaseHolder<T> extends RecyclerView.ViewHolder {

    protected T item;

    public FirebaseHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(int position);
    public abstract void bindAdmin(int position);
}
