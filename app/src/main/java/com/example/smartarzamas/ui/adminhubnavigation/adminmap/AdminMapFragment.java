package com.example.smartarzamas.ui.adminhubnavigation.adminmap;

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

import com.example.smartarzamas.AdminHubActivity;
import com.example.smartarzamas.HubActivity;
import com.example.smartarzamas.R;
import com.example.smartarzamas.databinding.NavigationFragmentMapBinding;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Locate;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.DialogAddLocate;
import com.example.smartarzamas.ui.OnDestroyListener;
import com.example.smartarzamas.ui.adminhubnavigation.AdminHubActivityCallback;
import com.example.smartarzamas.ui.adminhubnavigation.AdminHubNavigationCommon;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.example.smartarzamas.ui.hubnavigation.allchats.AllChatsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdminMapFragment extends AdminHubNavigationCommon implements OnMapReadyCallback {

    private AdminMapViewModel mapViewModel;
    private NavigationFragmentMapBinding binding;
    private static AdminMapFragmentCallback callback;
    private SupportMapFragment mapView;
    private GoogleMap googleMap;

    private ArrayList<Locate> locateMainList = new ArrayList<>();
    private ArrayList<Locate> locateList = new ArrayList<>();

    private FloatingActionButton fabAdd, fabCancel; // кнопки для взаимодействия с картой
    private boolean isAdd = false; // true = режим добавления метки

    private ValueEventListener locatesListener;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        locatesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (locateMainList.size() > 0) locateMainList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Locate l = (Locate) ds.getValue(Locate.class);
                    assert l != null;
                    locateMainList.add(l);
                }
                updateMapForView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbLocates.addValueEventListener(locatesListener);

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

    private void updateMapForView() {
        if (locateList.size() > 0) locateList.clear();
        for (Locate l : locateMainList){
            if (Utils.isEquals(searchString, l.name)){
                for (String cat : category){
                    if (l.category.equals(cat)){
                        locateList.add(l);
                        continue;
                    }
                }
            }
        }
        if (googleMap != null) {
            googleMap.clear();
            for (Locate l : locateList){
                if (googleMap != null) {
                    googleMap.addMarker(new MarkerOptions().title(l.name).snippet(l.description).position(l.getLocate()));
                }
            }
        }
    }

    @Override
    protected void addAdminHubActivityCallback() {
        AdminHubActivity.setAdminMapActivityCallback(new AdminHubActivityCallback() {
            @Override
            public void onCategoryChange(ArrayList<String> categories) {
                AdminMapFragment.this.category = categories;
                mapViewModel.setCategory(category);
                callback.onCategoryUpdate(category);
                updateMapForView();
            }

            @Override
            public void onSearchStringChange(String search) {
                AdminMapFragment.this.searchString = search;
                mapViewModel.setSearch(searchString);
                callback.onSearchUpdate(searchString);
                updateMapForView();
            }
        });
    }

    @Override
    protected void init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HubNavigationCommon.currentNavigationFragment = MAP;
        mapViewModel = new ViewModelProvider(this.getActivity()).get(AdminMapViewModel.class);
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
        dbLocates.removeEventListener(locatesListener);
        binding = null;
    }

    public static void setCallback(AdminMapFragmentCallback callback) {
        AdminMapFragment.callback = callback;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        googleMap = gMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.400135, 43.828324), 11));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if (isAdd) {
                    callback.onCreateLocate(AdminMapFragment.this, latLng);
                    isAdd = false;
                    fabCancel.setVisibility(View.GONE);
                    fabAdd.setVisibility(View.VISIBLE);
                }
            }
        });
        updateMapForView();
    }
}