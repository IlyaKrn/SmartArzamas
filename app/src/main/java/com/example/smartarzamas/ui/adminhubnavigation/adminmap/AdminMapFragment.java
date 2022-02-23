package com.example.smartarzamas.ui.adminhubnavigation.adminmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartarzamas.databinding.FragmentNotificationsBinding;

public class AdminMapFragment extends Fragment {

    private AdminMapViewModel adminMapViewModel;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adminMapViewModel = new ViewModelProvider(this).get(AdminMapViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}