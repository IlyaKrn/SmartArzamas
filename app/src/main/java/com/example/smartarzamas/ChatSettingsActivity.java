package com.example.smartarzamas;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.adapters.UserListAdapter;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.OnGetChat;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetUser;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.DialogChatDescriptionChange;
import com.example.smartarzamas.ui.DialogChatIconChange;
import com.example.smartarzamas.ui.DialogChatNameChange;
import com.example.smartarzamas.ui.OnIconChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatSettingsActivity extends FirebaseActivity {

    private ImageButton btClose;
    private Button btChangeName;
    private Button btChangeIcon;
    private Button btChangedDescription;
    private ImageView chatIcon;
    private TextView tvChatName;
    private TextView tvChatDescription;
    private ProgressBar progressBar;
    private RecyclerView rvMembers;
    private ArrayList<User> members = new ArrayList<User>();
    private UserListAdapter adapter;
    private ValueEventListener chatListener;
    private ValueEventListener usersListener;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        init();
        updateViewData();
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateViewData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dbChats.child(chat.id).addValueEventListener(chatListener);

        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (members.size() > 0)members.clear();
                for (DataSnapshot s : snapshot.getChildren()){
                    User u = (User) s.getValue(User.class);
                    assert u != null;
                    if(chat.isMember(u))
                        members.add(u);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dbUsers.addValueEventListener(usersListener);
        btChangedDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChatDescriptionChange dialog = new DialogChatDescriptionChange(ChatSettingsActivity.this, user, chat);
                dialog.create(R.id.fragmentContainerView);
            }
        });
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btChangeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChatIconChange dialog = new DialogChatIconChange(ChatSettingsActivity.this, user, chat);
                dialog.create(R.id.fragmentContainerView);
                dialog.setOnIconChangeListener(new OnIconChangeListener() {
                    @Override
                    public void onChange(Bitmap bitmap) {
                        chatIcon.setImageBitmap(bitmap);
                    }
                });
            }
        });
        btChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                    @Override
                    public void isConnected() {
                        DialogChatNameChange dialog = new DialogChatNameChange(ChatSettingsActivity.this, user, chat);
                        dialog.create(R.id.fragmentContainerView);
                    }
                });
            }
        });

    }
    // инициализация
    void init(){
        chat = (Chat) getIntent().getSerializableExtra(CHAT_ID);
        chatIcon = findViewById(R.id.chat_icon);
        tvChatName = findViewById(R.id.chat_name);
        tvChatDescription = findViewById(R.id.chat_description);
        progressBar = findViewById(R.id.progress);
        rvMembers = findViewById(R.id.rv_members);
        btChangeIcon = findViewById(R.id.bt_change_chat_icon);
        btChangeName = findViewById(R.id.bt_change_chat_name);
        btChangedDescription = findViewById(R.id.bt_change_chat_description);
        btClose = findViewById(R.id.bt_close);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserListAdapter(this, members, user, false, new UserListAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(int messagePosition) {

            }
        });
        rvMembers.setAdapter(adapter);
    }
    private void updateViewData(){
        if (chatIcon.getDrawable() == null){
            chatIcon.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        Chat.getChatById(chat.id, new OnGetChat() {
            @Override
            public void onGet(Chat chat) {
                ChatSettingsActivity.this.chat = chat;
                tvChatName.setText(chat.name);
                tvChatDescription.setText(chat.description);
                chat.getIconAsync(ChatSettingsActivity.this.getApplicationContext(), new OnGetIcon() {
                    public void onLoad(Bitmap bitmap) {
                        chatIcon.setImageBitmap(bitmap);
                        chatIcon.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    protected void onDestroy() {
        dbChats.removeEventListener(chatListener);
        dbUsers.removeEventListener(usersListener);
        super.onDestroy();
    }
}