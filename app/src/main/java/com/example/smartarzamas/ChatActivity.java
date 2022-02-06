package com.example.smartarzamas;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.adapters.MessageListAdapter;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Message;
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

    private ValueEventListener chatDataListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        getChatData();
    }

    // инициализация
    void init(){
        imagesForSend = new ArrayList<>();
        tvName = (TextView)findViewById(R.id.tvName);
        etSend = (EditText)findViewById(R.id.etSend);
        rvMessages = findViewById(R.id.rv_messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageListAdapter(this, messageList,/* userList,new ArrayList<>(), */ user, new MessageListAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(int messagePosition) {

            }
        });
        rvMessages.setAdapter(adapter);


        chatDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatActivity.this.chat = snapshot.getValue(Chat.class);
                tvName.setText(chat.name);
                if (messageList.size() > 0) messageList.clear();
                for (int i = 0; i < chat.messages.size(); i++) {
                    messageList.add(chat.messages.get(i));
                }
                adapter.notifyDataSetChanged();
                scrollMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    // получение информации о чате
    void getChatData(){
        Utils.isConnected(getApplicationContext(), new Utils.Connection() {
            @Override
            public void isConnected() {
                String id = getIntent().getStringExtra(CHAT_ID);
                if (id != null){
                    dbChats.child(id).addValueEventListener(chatDataListener);
                }

            }
        });
    }
    // переход к списку чатов
    public void onClose(View view) {
        finish();
    }
    // отпрака сообщения
    public void onSend(View view) {
        if (etSend.getText().toString().length() > 0) {
            String massege = etSend.getText().toString();
            if (messageList.get(0).equals(NULL_MESSAGE))
                messageList.remove(0);
            messageList.add(new Message(massege, user.id, dbChats.push().getKey(), imagesForSend));

            Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                @Override
                public void isConnected() {
                    dbChats.child(chat.id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.child("messages").getRef().setValue(messageList);
                            scrollMessages();
                            etSend.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            });

        }
    }
    private void scrollMessages(){
        rvMessages.scrollToPosition(messageList.size()-1);
    }
    // открытие меню чата
    public void onChatMenuOpen(View view) {
    }
    public void onAddImage(View view) {
    }

    @Override
    protected void onDestroy() {
        dbChats.child(chat.id).removeEventListener(chatDataListener);
        super.onDestroy();
    }
}