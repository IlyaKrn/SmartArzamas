package com.example.smartarzamas.ui.hubnavigation.allchats;

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
import com.example.smartarzamas.adapters.ChatListAdapter;
import com.example.smartarzamas.databinding.NavigationFragmentAllChatsBinding;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.support.SomethingMethods;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AllChatsFragment extends HubNavigationCommon {

    private AllChatsViewModel allChatsViewModel;
    private NavigationFragmentAllChatsBinding binding;
    private static AllChatsFragmentCallback callback;

    ArrayList<Chat> chatMainList = new ArrayList<>();
    ArrayList<Chat> chatList = new ArrayList<>();
    ChatListAdapter adapter;
    RecyclerView rvChats;
    FloatingActionButton fabAddChat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.currentNavigationFragment = ALL_CHATS;
        allChatsViewModel = new ViewModelProvider(this).get(AllChatsViewModel.class);
        binding = NavigationFragmentAllChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fabAddChat = binding.fab;
        rvChats = binding.rvChats;
        adapter = new ChatListAdapter(chatList, new ChatListAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(String chatId) {

            }
        });
        fabAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onCreateChat(AllChatsFragment.this);
            }
        });
        rvChats.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvChats.setAdapter(adapter);



        allChatsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        getAllChatList();
        return root;
    }

    // все чаты
    private void getAllChatList(){
        SomethingMethods.isConnected(getContext(), new SomethingMethods.Connection() {
            @Override
            public void isConnected() {
                dbChats.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (chatMainList.size() > 0) chatMainList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Chat c = (Chat) ds.getValue(Chat.class);
                            assert c != null;
                            chatMainList.add(c);
                        }
                        updateListForView();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });
    }

    @Override
    protected void addHubActivityCallback() {
        HubActivity.setAllChatsActivityCallback(new HubActivityCallback() {
            @Override
            public void onCategoryChange(ArrayList<String> categories) {
                AllChatsFragment.this.category = categories;
                updateListForView();
            }


            @Override
            public void onSearchStringChange(String search) {
                searchString = search;
                updateListForView();
            }
        });
    }

    private void updateListForView(){
        if (chatList.size() > 0) chatList.clear();
        for (Chat c : chatMainList){
            if (SomethingMethods.isEquals(searchString, c.name)){
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
    }
    public static void setCallback(AllChatsFragmentCallback callback) {
        AllChatsFragment.callback = callback;
    }
}