package com.example.smartarzamas.ui.adminhubnavigation.adminallchats;

import android.content.Intent;
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

import com.example.smartarzamas.AdminChatActivity;
import com.example.smartarzamas.FirebaseActivity;
import com.example.smartarzamas.HubActivity;
import com.example.smartarzamas.R;
import com.example.smartarzamas.adapters.ChatListAdapter;
import com.example.smartarzamas.adapters.FirebaseAdapter;
import com.example.smartarzamas.databinding.NavigationFragmentAllChatsBinding;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.OnGetListDataListener;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class AdminAllChatsFragment extends HubNavigationCommon {

    private AdminAllChatsViewModel allChatsViewModel;
    private NavigationFragmentAllChatsBinding binding;
    private static AdminAllChatsFragmentCallback callback;

    private ArrayList<Chat> chatMainList = new ArrayList<>();
    private ArrayList<Chat> chatList = new ArrayList<>();
    private ChatListAdapter adapter;
    private RecyclerView rvChats;
    private FloatingActionButton fabAddChat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Chat.addChatListListener("19", new OnGetListDataListener<Chat>() {
            @Override
            public void onGetData(ArrayList<Chat> data) {
                if (chatMainList.size() > 0) chatMainList.clear();
                chatMainList.addAll(data);
                updateListForView();
            }

            @Override
            public void onVoidData() {
                if (chatMainList.size() > 0) chatMainList.clear();
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

        adapter = new ChatListAdapter(getActivity().getApplicationContext(), user, true, chatList, new FirebaseAdapter.OnStateClickListener<Chat>() {
            @Override
            public void onClick(Chat item) {
                Intent intent = new Intent(AdminAllChatsFragment.this.getActivity(), AdminChatActivity.class);
                intent.putExtra(FirebaseActivity.CHAT_ID, item.id);
                intent.putExtra(FirebaseActivity.USER_INTENT, user);
                AdminAllChatsFragment.this.getActivity().startActivity(intent);
            }

            @Override
            public void onLongClick(Chat item) {

            }
        });
        fabAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onCreateChat(AdminAllChatsFragment.this);
            }
        });
        rvChats.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvChats.setAdapter(adapter);



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
                AdminAllChatsFragment.this.category = categories;
                allChatsViewModel.setCategory(category);
                callback.onCategoryUpdate(category);
                updateListForView();
            }

            @Override
            public void onSearchStringChange(String search) {
                AdminAllChatsFragment.this.searchString = search;
                allChatsViewModel.setSearch(searchString);
                callback.onSearchUpdate(searchString);
                updateListForView();
            }
        });
    }

    @Override
    protected void init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HubNavigationCommon.currentNavigationFragment = ALL_CHATS;
        allChatsViewModel = new ViewModelProvider(this.getActivity()).get(AdminAllChatsViewModel.class);
        binding = NavigationFragmentAllChatsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        fabAddChat = binding.fab;
        rvChats = binding.rvChats;
    }

    private void updateListForView(){
        if (chatList.size() > 0) chatList.clear();
        for (Chat c : chatMainList){
            if (Utils.isEquals(searchString, c.name)){
                for (String cat : category){
                    if (c.category.equals(cat)){
                        chatList.add(c);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Chat.removeDataListener("19");
    }
    public static void setCallback(AdminAllChatsFragmentCallback callback) {
        AdminAllChatsFragment.callback = callback;
    }
}