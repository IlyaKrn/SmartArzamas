package com.example.smartarzamas.ui.hubnavigation.mychats;

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
import com.example.smartarzamas.databinding.NavigationFragmentMyChatsBinding;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.SomethingMethods;
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

    ArrayList<Chat> chatMainList = new ArrayList<>();
    ArrayList<Chat> chatList = new ArrayList<>();
    ChatListAdapter adapter;
    RecyclerView rvChats;
    static User user;
    FloatingActionButton fabAddChat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.currentNavigationFragment = MY_CHATS;
        myChatsViewModel = new ViewModelProvider(this).get(MyChatsViewModel.class);
        binding = NavigationFragmentMyChatsBinding.inflate(inflater, container, false);
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
                callback.onCreateChat(MyChatsFragment.this);
            }
        });
        rvChats.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvChats.setAdapter(adapter);


        myChatsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

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
                updateListForView();
            }

            @Override
            public void onSearchStringChange(String search) {
                searchString = search;
                updateListForView();
            }
        });
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
                            for (String email : c.membersEmailList){
                                if (user.email.equals(email)){
                                    chatMainList.add(c);
                                }
                            }
                        }
                        updateListForView();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
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

    public static void setCallback(MyChatsFragmentCallback callback) {
        MyChatsFragment.callback = callback;
    }

    public static void setUser(User user) {
        MyChatsFragment.user = user;
    }
}