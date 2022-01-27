package com.example.smartarzamas.ui.hubnavigation.allchats;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public interface AllChatsFragmentCallback {
     public void onCreateChat(Fragment fragment);
     public void onSearchUpdate(String search);
     public void onCategoryUpdate(ArrayList<String> category);

}
