package com.example.smartarzamas.ui.adminhubnavigation.adminallchats;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public interface AdminAllChatsFragmentCallback {
    public void onCreateChat(Fragment fragment);
    public void onSearchUpdate(String search);
    public void onCategoryUpdate(ArrayList<String> category);

}
