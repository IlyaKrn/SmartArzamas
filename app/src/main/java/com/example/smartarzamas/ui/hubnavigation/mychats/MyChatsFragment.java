package com.example.smartarzamas.ui.hubnavigation.mychats;

import android.content.Intent;
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

import com.example.smartarzamas.ChatActivity;
import com.example.smartarzamas.FirebaseActivity;
import com.example.smartarzamas.HubActivity;
import com.example.smartarzamas.adapters.ChatListAdapter;
import com.example.smartarzamas.databinding.NavigationFragmentMyChatsBinding;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyChatsFragment extends HubNavigationCommon {

    private MyChatsViewModel myChatsViewModel;
    private NavigationFragmentMyChatsBinding binding;
    private static MyChatsFragmentCallback callback;

    private ArrayList<Chat> chatMainList = new ArrayList<>();
    private ArrayList<Chat> chatList = new ArrayList<>();
    private ChatListAdapter adapter;
    private RecyclerView rvChats;
    private FloatingActionButton fabAddChat;

    private ValueEventListener chatListListener;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        chatListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (chatMainList.size() > 0) chatMainList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat c = (Chat) ds.getValue(Chat.class);
                    assert c != null;
                    for (String email : c.membersEmailList){
                        if (user.email.equals(email)){
                            chatMainList.add(c);
                        }
                    }
                }
                updateListForView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        adapter = new ChatListAdapter(getActivity().getApplicationContext(), chatList, user, false, new ChatListAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(String chatId) {
                Intent intent = new Intent(MyChatsFragment.this.getActivity(), ChatActivity.class);
                intent.putExtra(FirebaseActivity.CHAT_ID, chatId);
                intent.putExtra(FirebaseActivity.USER_INTENT, user);
                MyChatsFragment.this.getActivity().startActivity(intent);
            }
        });
        fabAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onCreateChat(MyChatsFragment.this);
            }
        });
        rvChats.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvChats.setAdapter(adapter);


        myChatsViewModel.getSearch().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                searchString = s;
                callback.onSearchUpdate(s);
            }
        });
        myChatsViewModel.getCategory().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                category = strings;
                callback.onCategoryUpdate(strings);
            }
        });
        getAllChatList();
        return root;
    }

    @Override
    protected void addHubActivityCallback() {
        HubActivity.setMyChatsActivityCallback(new HubActivityCallback() {
            @Override
            public void onCategoryChange(ArrayList<String> categories) {
                MyChatsFragment.this.category = categories;
                myChatsViewModel.setCategory(category);
                callback.onCategoryUpdate(category);
                updateListForView();
            }

            @Override
            public void onSearchStringChange(String search) {
                MyChatsFragment.this.searchString = search;
                myChatsViewModel.setSearch(searchString);
                callback.onSearchUpdate(searchString);
                updateListForView();
            }
        });
    }

    @Override
    protected void init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HubNavigationCommon.currentNavigationFragment = MY_CHATS;
        myChatsViewModel = new ViewModelProvider(this.getActivity()).get(MyChatsViewModel.class);
        binding = NavigationFragmentMyChatsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        fabAddChat = binding.fab;
        rvChats = binding.rvChats;
    }

    // все чаты
    private void getAllChatList(){
        Utils.isConnected(getContext(), new Utils.Connection() {
            @Override
            public void isConnected() {
                dbChats.addValueEventListener(chatListListener);
            }
        });
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
        dbChats.removeEventListener(chatListListener);
    }

    public static void setCallback(MyChatsFragmentCallback callback) {
        MyChatsFragment.callback = callback;
    }

}