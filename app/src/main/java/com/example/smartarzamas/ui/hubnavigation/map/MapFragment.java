package com.example.smartarzamas.ui.hubnavigation.map;

import android.os.Bundle;
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
import com.example.smartarzamas.support.SomethingMethods;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapFragment extends HubNavigationCommon {

    private MapViewModel mapViewModel;
    private NavigationFragmentMapBinding binding;
    private static MapFragmentCallback callback;

    private FloatingActionButton fabAdd, fabCancel; // кнопки для взаимодействия с картой
    private boolean isAdd = false; // true = режим добавления метки


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.currentNavigationFragment = MAP;
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding = NavigationFragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fabAdd = binding.fabAdd;
        fabCancel = binding.fabCancel;

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



        mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    @Override
    protected void addHubActivityCallback() {
        HubActivity.setMapActivityCallback(new HubActivityCallback() {
            @Override
            public void onCategoryChange(ArrayList<String> categories) {
                if (binding != null) {
                    SomethingMethods.isConnected(MapFragment.this.getActivity().getApplicationContext(), new SomethingMethods.Connection() {
                        @Override
                        public void isConnected() {
                            dbLocates.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                           /*     if (chatList.size() > 0) chatList.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Chat c = (Chat) ds.getValue(Chat.class);
                                    assert c != null;
                                    for (String cat : categories) {
                                        if (c.category.equals(cat)) {
                                            chatList.add(c);
                                            break;
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();


                            */
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onSearchStringChange(String search) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void setCallback(MapFragmentCallback callback) {
        MapFragment.callback = callback;
    }


}