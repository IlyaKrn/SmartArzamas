package com.example.smartarzamas.ui.hubnavigation;

import java.util.ArrayList;

public interface HubActivityCallback {
    public void onCategoryChange(ArrayList<String> categories);
    public void onSearchStringChange(String search);
}
