package com.example.smartarzamas.firebaseobjects;

import android.content.Context;

import com.google.firebase.database.DatabaseError;

public interface OnGetDataListener<T extends FirebaseObject> {
    void onGetData(T data);
    void onVoidData();
    void onNoConnection();
    void onCanceled();

}
