package com.example.smartarzamas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.adapters.MessageListAdapter;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.FirebaseObject;
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnSetIcon;
import com.example.smartarzamas.support.Category;
import com.example.smartarzamas.support.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends FirebaseActivity {

    private TextView tvName;
    private RecyclerView rvMessages;
    private EditText etSend;
    private ArrayList<Message> messageList  = new ArrayList<>();;
    private MessageListAdapter adapter;
    public final String NULL_MESSAGE = "";
    private static ArrayList<Message> selectedIds  = new ArrayList<>();;
    private ArrayList<Bitmap> imagesForSend  = new ArrayList<>();
    private Chat chat;

    private ImageButton btSend;
    private ImageButton btAddImage;
    private ImageButton btChatSettings;
    private ImageButton btClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        Chat.getChatById(getIntent().getStringExtra(CHAT_ID), new OnGetDataListener<Chat>() {
            @Override
            public void onGetData(Chat data) {
                ChatActivity.this.chat = data;
                updateViewData();
                chat.addChatListener("11", new OnGetDataListener<Chat>() {
                    @Override
                    public void onGetData(Chat data) {
                        ChatActivity.this.chat = data;
                        updateViewData();
                    }

                    @Override
                    public void onVoidData() {

                    }

                    @Override
                    public void onNoConnection() {

                    }

                    @Override
                    public void onCanceled() {

                    }
                });
            }

            @Override
            public void onVoidData() {

            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {

            }
        });
    }

    // инициализация
    void init(){
        imagesForSend = new ArrayList<>();
        tvName = (TextView)findViewById(R.id.tv_name);
        etSend = (EditText)findViewById(R.id.et_send);
        rvMessages = findViewById(R.id.rv_messages);
        btSend = findViewById(R.id.bt_send);
        btAddImage = findViewById(R.id.bt_set_image);
        btChatSettings = findViewById(R.id.bt_chat_menu);
        btClose = findViewById(R.id.bt_close);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageListAdapter(this, messageList,/* userList,new ArrayList<>(), */ user, false, new MessageListAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(int messagePosition) {

            }
        });
        rvMessages.setAdapter(adapter);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etSend.getText().toString().length() > 0) {
                    String massege = etSend.getText().toString();
                    if (messageList.get(0).equals(NULL_MESSAGE))
                        messageList.remove(0);
                    Message m = new Message(massege, user.id, dbChats.push().getKey(), null);
                    if (imagesForSend.size() > 0) {
                        final int[] count = {0};
                        for (int i = 0; i < imagesForSend.size(); i++) {
                            m.addImageAsync(getApplicationContext(), chat, imagesForSend.get(i), new OnSetIcon() {
                                @Override
                                public void onSet(String ref, Bitmap bitmap) {
                                    count[0]++;
                                    if (count[0] == imagesForSend.size()) {
                                        messageList.add(m);
                                        Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                                            @Override
                                            public void isConnected() {
                                                dbChats.child(chat.id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        dataSnapshot.child("messages").getRef().setValue(messageList);
                                                        scrollMessages();
                                                        etSend.setText("");
                                                        imagesForSend.clear();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                    else {
                        messageList.add(m);
                        Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                            @Override
                            public void isConnected() {
                                dbChats.child(chat.id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        dataSnapshot.child("messages").getRef().setValue(messageList);
                                        scrollMessages();
                                        etSend.setText("");
                                        imagesForSend.clear();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
        btAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
        btChatSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(ChatActivity.this, view);
                popup.inflate(R.menu.popup_menu_chat);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            // ямы на дорогах
                            case R.id.chat_settings:
                                Intent intent = new Intent(ChatActivity.this, ChatSettingsActivity.class);
                                intent.putExtra(CHAT_ID, chat);
                                intent.putExtra(USER_INTENT, user);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void scrollMessages(){
        rvMessages.scrollToPosition(messageList.size()-1);
    }
    private void updateViewData(){
        tvName.setText(chat.name);
        if (messageList.size() > 0) messageList.clear();
        for (int i = 0; i < chat.messages.size(); i++) {
            messageList.add(chat.messages.get(i));
        }
        adapter.notifyDataSetChanged();
        scrollMessages();
    }
    @Override
    protected void onDestroy() {
        chat.removeObjectDataListener("11");
        super.onDestroy();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null && requestCode == 1){
            ImageView iv = new ImageView(this);
            iv.setImageURI(data.getData());
            imagesForSend.add(((BitmapDrawable) iv.getDrawable()).getBitmap());
        }
    }
}