package com.example.smartarzamas.ui.adminhubnavigation.adminallusers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartarzamas.AdminHubActivity;
import com.example.smartarzamas.databinding.FragmentDashboardBinding;
import com.example.smartarzamas.ui.adminhubnavigation.AdminHubActivityCallback;
import com.example.smartarzamas.ui.adminhubnavigation.AdminHubNavigationCommon;

import java.util.ArrayList;

public class AdminAllUsersFragment extends AdminHubNavigationCommon {

    private AdminAllUsersViewModel adminAllUsersViewModel;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adminAllUsersViewModel = new ViewModelProvider(this).get(AdminAllUsersViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    protected void addAdminHubActivityCallback() {
        AdminHubActivity.setMyChatsActivityCallback(new AdminHubActivityCallback() {
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}