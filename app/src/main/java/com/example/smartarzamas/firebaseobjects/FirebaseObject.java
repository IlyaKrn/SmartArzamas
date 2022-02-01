package com.example.smartarzamas.firebaseobjects;

import com.google.firebase.database.DatabaseReference;

public abstract class FirebaseObject {

    public static final String LOG_TAG = "FirebaseObject";
    public String name;
    public String id;

    public FirebaseObject(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public FirebaseObject() {
    }

    public static DatabaseReference getDatabase() {
        return null;
    }

    public abstract void getIconAsync(OnLoadBitmap onLoadBitmap);
}
