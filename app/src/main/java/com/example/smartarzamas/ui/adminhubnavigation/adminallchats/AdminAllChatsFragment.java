package com.example.smartarzamas.ui.adminhubnavigation.adminallchats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartarzamas.databinding.FragmentHomeBinding;

public class AdminAllChatsFragment extends Fragment {

    private AdminAllChatsViewModel adminAllChatsViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adminAllChatsViewModel = new ViewModelProvider(this).get(AdminAllChatsViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}