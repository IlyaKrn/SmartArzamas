package com.example.smartarzamas.ui.hubnavigation.map;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface MapFragmentCallback {

    void onSearchUpdate(String search);
    void onCategoryUpdate(ArrayList<String> category);
    void onCreateLocate(Fragment fragment, LatLng latLng);
}
