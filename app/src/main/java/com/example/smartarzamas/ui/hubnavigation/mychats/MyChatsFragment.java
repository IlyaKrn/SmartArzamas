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

    ArrayList<Chat> chatList = new ArrayList<>();
    ChatListAdapter adapter;
    RecyclerView rvChats;
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

        HubActivity.setMyChatsActivityCallback(new HubActivityCallback() {
            @Override
            public void onCategoryChange(ArrayList<String> categories, User user) {
                if (binding != null) {
                    SomethingMethods.isConnected(MyChatsFragment.this.getActivity().getApplicationContext(), new SomethingMethods.Connection() {
                        @Override
                        public void isConnected() {
                            Chat.getDatabase().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (chatList.size() > 0) chatList.clear();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Chat c = (Chat) ds.getValue(Chat.class);
                                        assert c != null;
                                        select:
                                        for (String member : c.membersEmailList) {
                                            if (member.equals(user.email)) {
                                                for (String cat : categories) {
                                                    if (c.category.equals(cat)) {
                                                        chatList.add(c);
                                                        break select;
                                                    }
                                                }
                                            }
                                        }


                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onSearchStringChange(String search) {
                if (chatList.size() > 0) chatList.clear();
                for (Chat c : chatList){
                    if (SomethingMethods.isEquals(c.name, search)){
                        chatList.add(c);
                    }
                }
            }
        });

        myChatsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void setCallback(MyChatsFragmentCallback callback) {
        MyChatsFragment.callback = callback;
    }
}