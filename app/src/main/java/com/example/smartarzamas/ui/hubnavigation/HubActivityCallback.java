package com.example.smartarzamas.ui.hubnavigation;

import com.example.smartarzamas.firebaseobjects.User;

import java.util.ArrayList;

public interface HubActivityCallback {
    public void onCategoryChange(ArrayList<String> categories, User user);
    public void onSearchStringChange(String search);
}
