package com.example.smartarzamas;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.OnGetChat;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetUser;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.DialogUserIconChange;
import com.example.smartarzamas.ui.DialogUserNameAndFamilyChange;
import com.example.smartarzamas.ui.OnIconChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ChatSettingsActivity extends FirebaseActivity {

    private ImageView chatIcon;
    private TextView chatName;
    private ProgressBar progressBar;
    private ValueEventListener chatListener;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
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
    }
    // инициализация
    void init(){
        chat = (Chat) getIntent().getSerializableExtra(CHAT_ID);
        chatIcon = findViewById(R.id.user_icon);
        chatName = findViewById(R.id.user_name);
        progressBar = findViewById(R.id.progress);
    }
    // изменение имени и фамилии
    public void onChangeUserNameAndFamily(View view) {
        Utils.isConnected(getApplicationContext(), new Utils.Connection() {
            @Override
            public void isConnected() {
                DialogUserNameAndFamilyChange dialog = new DialogUserNameAndFamilyChange(ChatSettingsActivity.this, user);
                dialog.create(R.id.fragmentContainerView);
            }
        });
    }
    // закрытие активности
    public void onCloseInfo(View view) {
        finish();
    }

    public void onDeleteAccount(View view) {

    }

    public void onChangeUserIcon(View view) {
        DialogUserIconChange dialog = new DialogUserIconChange(ChatSettingsActivity.this, user);
        dialog.create(R.id.fragmentContainerView);
        dialog.setOnIconChangeListener(new OnIconChangeListener() {
            @Override
            public void onChange(Bitmap bitmap) {
                chatIcon.setImageBitmap(bitmap);
            }
        });

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
                chatName.setText(chat.name);
                chat.getIconAsync(ChatSettingsActivity.this.getApplicationContext(), new OnGetIcon() {
                    public void onLoad(Bitmap bitmap) {
                        chatIcon.setImageBitmap(bitmap);
                        chatIcon.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        dbChats.removeEventListener(chatListener);
        super.onDestroy();
    }
}