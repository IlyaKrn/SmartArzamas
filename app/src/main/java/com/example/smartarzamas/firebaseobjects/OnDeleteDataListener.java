package com.example.smartarzamas.firebaseobjects;

import com.google.firebase.database.DatabaseReference;

public interface OnDeleteDataListener {
    void onDataDelete(DatabaseReference deleteRef);
    void onNoConnection();
    void onCanceled();
}
