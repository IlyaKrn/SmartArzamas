package com.example.smartarzamas.ui.adminhubnavigation.adminallusers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartarzamas.databinding.FragmentDashboardBinding;

public class AdminAllUsersFragment extends Fragment {

    private AdminAllUsersViewModel adminAllUsersViewModel;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adminAllUsersViewModel = new ViewModelProvider(this).get(AdminAllUsersViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}