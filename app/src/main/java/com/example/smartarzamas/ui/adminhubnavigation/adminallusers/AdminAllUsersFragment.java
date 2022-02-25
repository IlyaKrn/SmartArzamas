package com.example.smartarzamas.ui.adminhubnavigation.adminallusers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.HubActivity;
import com.example.smartarzamas.adapters.UserListAdapter;
import com.example.smartarzamas.databinding.NavigationFragmentAllChatsBinding;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdminAllUsersFragment extends HubNavigationCommon {

    private AdminAllUsersViewModel allChatsViewModel;
    private NavigationFragmentAllChatsBinding binding;
    private static AdminAllUsersFragmentCallback callback;

    private ArrayList<User> userMainList = new ArrayList<>();
    private ArrayList<User> chatList = new ArrayList<>();
    private UserListAdapter adapter;
    private RecyclerView rvUsers;

    private ValueEventListener userListListener;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        userListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (userMainList.size() > 0) userMainList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User u = (User) ds.getValue(User.class);
                    assert u != null;
                    userMainList.add(u);
                }
                updateListForView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        adapter = new UserListAdapter(getActivity().getApplicationContext(), chatList, user, true, new UserListAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(int messagePosition) {

            }
        });
        rvUsers.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvUsers.setAdapter(adapter);



        allChatsViewModel.getSearch().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                searchString = s;
                callback.onSearchUpdate(s);
            }
        });
        allChatsViewModel.getCategory().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                category = strings;
                callback.onCategoryUpdate(strings);
            }
        });

        getAllChatList();
        return root;
    }

    // все чаты
    private void getAllChatList(){
        Utils.isConnected(getContext(), new Utils.Connection() {
            @Override
            public void isConnected() {
                dbUsers.addValueEventListener(userListListener);
            }
        });
    }

    @Override
    protected void addHubActivityCallback() {
        HubActivity.setAllChatsActivityCallback(new HubActivityCallback() {
            @Override
            public void onCategoryChange(ArrayList<String> categories) {
                AdminAllUsersFragment.this.category = categories;
                allChatsViewModel.setCategory(category);
                callback.onCategoryUpdate(category);
                updateListForView();
            }

            @Override
            public void onSearchStringChange(String search) {
                AdminAllUsersFragment.this.searchString = search;
                allChatsViewModel.setSearch(searchString);
                callback.onSearchUpdate(searchString);
                updateListForView();
            }
        });
    }

    @Override
    protected void init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HubNavigationCommon.currentNavigationFragment = ALL_CHATS;
        allChatsViewModel = new ViewModelProvider(this.getActivity()).get(AdminAllUsersViewModel.class);
        binding = NavigationFragmentAllChatsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        rvUsers = binding.rvChats;
    }

    private void updateListForView(){
        if (chatList.size() > 0) chatList.clear();
        for (User u : userMainList){
            if (Utils.isEquals(searchString, u.name)){
                chatList.add(u);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        dbUsers.removeEventListener(userListListener);
    }
    public static void setCallback(AdminAllUsersFragmentCallback callback) {
        AdminAllUsersFragment.callback = callback;
    }
}