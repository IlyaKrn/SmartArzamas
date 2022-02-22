package com.example.smartarzamas.ui.adminhubnavigation.adminmap;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface AdminMapFragmentCallback {

    void onSearchUpdate(String search);
    void onCategoryUpdate(ArrayList<String> category);
    void onCreateLocate(Fragment fragment, LatLng latLng);
}
