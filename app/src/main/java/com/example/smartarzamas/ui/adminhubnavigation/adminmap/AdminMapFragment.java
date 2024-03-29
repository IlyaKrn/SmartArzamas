package com.example.smartarzamas.ui.adminhubnavigation.adminmap;

import android.graphics.Bitmap;
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
import com.example.smartarzamas.adapters.MapInfoWindowAdapter;
import com.example.smartarzamas.databinding.FragmentNotificationsBinding;
import com.example.smartarzamas.firebaseobjects.Locate;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetListDataListener;
import com.example.smartarzamas.firebaseobjects.OnSetDataListener;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.DialogConfirm;
import com.example.smartarzamas.ui.OnConfirmListener;
import com.example.smartarzamas.ui.adminhubnavigation.AdminHubActivityCallback;
import com.example.smartarzamas.ui.adminhubnavigation.AdminHubNavigationCommon;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.example.smartarzamas.ui.hubnavigation.map.MapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AdminMapFragment extends AdminHubNavigationCommon implements OnMapReadyCallback {

    private AdminMapViewModel adminMapViewModel;
    private FragmentNotificationsBinding binding;
    private static AdminMapFragmentCallback callback;
    private SupportMapFragment mapView;
    private GoogleMap googleMap;
    private MapInfoWindowAdapter mapAdapter;

    private ArrayList<Locate> locateMainList = new ArrayList<>();
    private ArrayList<Locate> locateList = new ArrayList<>();

    private FloatingActionButton fabAdd, fabCancel; // кнопки для взаимодействия с картой
    private boolean isAdd = false; // true = режим добавления метки

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Locate.addLocateListListener(getContext(), "22", new OnGetListDataListener<Locate>() {
            @Override
            public void onGetData(ArrayList<Locate> data) {
                if (locateMainList.size() > 0) locateMainList.clear();
                locateMainList.addAll(data);
                Map<Locate, Bitmap> cache = new HashMap<>();
                final int[] count = {0};
                for (Locate l : locateMainList){
                    l.getIconAsync(getContext(), new OnGetIcon() {
                        @Override
                        public void onLoad(Bitmap bitmap) {
                            count[0]++;
                            cache.put(l, bitmap);
                            Log.e("ffff", "fffffff");
                            if (locateMainList.size() == count[0])
                                mapAdapter.loadCache(cache);
                        }
                    });
                }
                updateMapForView();
            }

            @Override
            public void onVoidData() {
                if (locateMainList.size() > 0) locateMainList.clear();
                updateMapForView();
            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

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



        adminMapViewModel.getSearch().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                searchString = s;
                callback.onSearchUpdate(s);
            }
        });
        adminMapViewModel.getCategory().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                category = strings;
                callback.onCategoryUpdate(strings);
            }
        });
        mapAdapter = new MapInfoWindowAdapter(getContext(), user, locateList);
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
                    googleMap.addMarker(new MarkerOptions().position(l.locate()));
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
                adminMapViewModel.setCategory(category);
                callback.onCategoryUpdate(category);
                updateMapForView();
            }

            @Override
            public void onSearchStringChange(String search) {
                AdminMapFragment.this.searchString = search;
                adminMapViewModel.setSearch(searchString);
                callback.onSearchUpdate(searchString);
                updateMapForView();
            }
        });
    }

    @Override
    protected void init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AdminHubNavigationCommon.currentNavigationFragment = AdminHubNavigationCommon.MAP;
        adminMapViewModel = new ViewModelProvider(this.getActivity()).get(AdminMapViewModel.class);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        mapView = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapView.getMapAsync(this);
        fabAdd = binding.fabAdd;
        fabCancel = binding.fabCancel;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Locate.removeDataListener("22");
        binding = null;
    }

    public static void setCallback(AdminMapFragmentCallback callback) {
        AdminMapFragment.callback = callback;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setInfoWindowAdapter(mapAdapter);
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
        googleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(@NonNull Marker marker) {
                DialogConfirm dialog = new DialogConfirm(AdminMapFragment.this, "Удаление", "Удалить", "Вы действительно хотите удалить метку?", new OnConfirmListener() {
                    @Override
                    public void onConfirm(DialogConfirm d) {
                        d.freeze();
                        for (Locate l : locateMainList) {
                            if (l.locate().equals(marker.getPosition())) {
                                l.setNewData(getContext(), null, new OnSetDataListener<Locate>() {
                                    @Override
                                    public void onSetData(Locate data) {
                                        Toast.makeText(getContext(), "Метка удалена", Toast.LENGTH_LONG).show();
                                        d.destroy();
                                    }

                                    @Override
                                    public void onNoConnection() {

                                    }

                                    @Override
                                    public void onCanceled() {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancel(DialogConfirm d) {
                        d.destroy();
                    }
                });
                dialog.create(R.id.fragmentContainerView);
            }
        });


      updateMapForView();
    }
}