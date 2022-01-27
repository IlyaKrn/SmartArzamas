package com.example.smartarzamas.ui.hubnavigation.mychats;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public interface MyChatsFragmentCallback {
    void onCreateChat(Fragment fragment);
    public void onSearchUpdate(String search);
    public void onCategoryUpdate(ArrayList<String> category);
}
