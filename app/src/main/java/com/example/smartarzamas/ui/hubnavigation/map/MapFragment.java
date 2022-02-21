package com.example.smartarzamas.ui.hubnavigation.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartarzamas.HubActivity;
import com.example.smartarzamas.R;
import com.example.smartarzamas.databinding.NavigationFragmentMapBinding;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MapFragment extends HubNavigationCommon implements OnMapReadyCallback {

    private MapViewModel mapViewModel;
    private NavigationFragmentMapBinding binding;
    private static MapFragmentCallback callback;
    private SupportMapFragment mapView;
    private GoogleMap googleMap;

    private FloatingActionButton fabAdd, fabCancel; // кнопки для взаимодействия с картой
    private boolean isAdd = false; // true = режим добавления метки


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View.OnClickListener onAddListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabCancel.setVisibility(View.VISIBLE);
                fabAdd.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                if (isAdd){
                    isAdd = false;
                    Toast.makeText(getActivity().getApplicationContext(), R.string.add_locate_mod_disabled, Toast.LENGTH_SHORT).show();
                }
                else {
                    isAdd = true;
                    Toast.makeText(getActivity().getApplicationContext(), R.string.add_locate_mod_enabled, Toast.LENGTH_SHORT).show();
                }

            }
        };

        fabAdd.setOnClickListener(onAddListener);
        fabCancel.setOnClickListener(onAddListener);



        mapViewModel.getSearch().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                searchString = s;
                callback.onSearchUpdate(s);
            }
        });
        mapViewModel.getCategory().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                category = strings;
                callback.onCategoryUpdate(strings);
            }
        });
        return root;
    }

    @Override
    protected void addHubActivityCallback() {
        HubActivity.setMapActivityCallback(new HubActivityCallback() {
            @Override
            public void onCategoryChange(ArrayList<String> categories) {

            }

            @Override
            public void onSearchStringChange(String search) {

            }
        });
    }

    @Override
    protected void init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HubNavigationCommon.currentNavigationFragment = MAP;
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding = NavigationFragmentMapBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        mapView = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapView.getMapAsync(this);
        fabAdd = binding.fabAdd;
        fabCancel = binding.fabCancel;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void setCallback(MapFragmentCallback callback) {
        MapFragment.callback = callback;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        googleMap = gMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.400135, 43.828324), 11));
    }
}