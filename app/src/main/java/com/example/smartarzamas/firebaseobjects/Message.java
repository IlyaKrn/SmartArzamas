package com.example.smartarzamas.firebaseobjects;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Message {
    public String message;
    public String userEmail;
    public ArrayList<Bitmap> images;

    public Message(String message, String user, ArrayList<Bitmap> images) {
        this.message = message;
        this.userEmail = user;
        this.images = images;
    }

    public Message() {
    }
}
