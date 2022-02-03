package com.example.smartarzamas.firebaseobjects;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Message {
    public String id;
    public String message;
    public String userId;
    public ArrayList<Bitmap> images;

    public Message(String message, String user, String id, ArrayList<Bitmap> images) {
        this.id = id;
        this.message = message;
        this.userId = user;
        this.images = images;
    }

    public Message() {
    }
}
