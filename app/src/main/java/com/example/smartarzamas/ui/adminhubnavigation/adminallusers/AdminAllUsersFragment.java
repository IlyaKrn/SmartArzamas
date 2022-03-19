package com.example.smartarzamas.ui.adminhubnavigation.adminallusers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.HubActivity;
import com.example.smartarzamas.R;
import com.example.smartarzamas.adapters.FirebaseAdapter;
import com.example.smartarzamas.adapters.UserListAdapter;
import com.example.smartarzamas.databinding.NavigationFragmentAllChatsBinding;
import com.example.smartarzamas.firebaseobjects.OnGetListDataListener;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;

import java.util.ArrayList;


public class AdminAllUsersFragment extends HubNavigationCommon {

    private AdminAllUsersViewModel allChatsViewModel;
    private NavigationFragmentAllChatsBinding binding;
    private static AdminAllUsersFragmentCallback callback;

    private ArrayList<User> userMainList = new ArrayList<>();
    private ArrayList<User> chatList = new ArrayList<>();
    private UserListAdapter adapter;
    private RecyclerView rvUsers;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        User.addUserListListener(getContext(), "20", new OnGetListDataListener<User>() {
            @Override
            public void onGetData(ArrayList<User> data) {
                if (userMainList.size() > 0) userMainList.clear();
                for (User u : data) {
                    userMainList.add(u);
                }
                updateListForView();
            }

            @Override
            public void onVoidData() {
                if (userMainList.size() > 0) userMainList.clear();
                updateListForView();
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

        adapter = new UserListAdapter(getActivity().getApplicationContext(), user, true, chatList, new FirebaseAdapter.OnStateClickListener<User>() {
            @Override
            public void onClick(User item) {

            }

            @Override
            public void onLongClick(User item) {

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

        return root;
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
        User.removeDataListener("20");
    }
    public static void setCallback(AdminAllUsersFragmentCallback callback) {
        AdminAllUsersFragment.callback = callback;
    }
}