package com.example.smartarzamas.ui.adminhubnavigation.adminallusers;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public interface AdminAllUsersFragmentCallback {
    void onCreateChat(Fragment fragment);
    public void onSearchUpdate(String search);
    public void onCategoryUpdate(ArrayList<String> category);
}
