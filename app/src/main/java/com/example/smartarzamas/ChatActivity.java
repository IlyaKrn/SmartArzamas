package com.example.smartarzamas;

import android.content.Intent;
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
import com.example.smartarzamas.firebaseobjects.OnGetChat;
import com.example.smartarzamas.support.SomethingMethods;
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
    Chat chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        getChatData();
     //   getUserList();
    }

    // инициализация
    void init(){
        imagesForSend = new ArrayList<>();
        tvName = (TextView)findViewById(R.id.tvName);
        etSend = (EditText)findViewById(R.id.etSend);
        rvMessages = findViewById(R.id.rv_messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageListAdapter(messageList,/* userList,*/new ArrayList<>(),  user, new MessageListAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(int messagePosition) {

            }
        });
        rvMessages.setAdapter(adapter);
    }

    /*
    // получение списка ползователей
    private void getUserList() {
        SomethingMethods.isConnected(getApplicationContext(), new SomethingMethods.Connection() {
            @Override
            public void isConnected() {
                dbUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User u =  ds.getValue(User.class);
                            assert u != null;
                            userList.add(u);

                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });
    }

     */
    // получение информации о чате
    void getChatData(){
        SomethingMethods.isConnected(getApplicationContext(), new SomethingMethods.Connection() {
            @Override
            public void isConnected() {
                String id = getIntent().getStringExtra(CHAT_ID);
                if (id != null){
                    Chat.getChatById(id, new OnGetChat() {
                        @Override
                        public void onGet(Chat chat) {
                            ChatActivity.this.chat = chat;
                            tvName.setText(chat.name);
                            if (messageList.size() > 0) messageList.clear();
                            for (int i = 0; i < chat.messages.size(); i++) {
                                messageList.add(chat.messages.get(i));
                            }
                            adapter.notifyDataSetChanged();
                            rvMessages.scrollToPosition(messageList.size()-1);
                        }
                    });
                }

                /*
                dbChats.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String id = getIntent().getExtras().getString("chat_id");
                            Chat c = ds.getValue(Chat.class);
                            assert c != null;
                            if (c.id.equals(id)) {

                            }
                        }
                        adapter.notifyDataSetChanged();
                        rvMessages.scrollToPosition(messageList.size()-1);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

                 */
            }
        });
    }
    // переход к списку чатов
    public void onClose(View view) {
        Intent intent = new Intent(ChatActivity.this, HubActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
    // отпрака сообщения
    public void onSend(View view) {
        if (etSend.getText().toString().length() > 0) {
            String massege = etSend.getText().toString();
            if (messageList.get(0).equals(NULL_MESSAGE))
                messageList.remove(0);
            messageList.add(new Message(massege,/* user.email*/ null, dbChats.push().getKey(), imagesForSend));

            SomethingMethods.isConnected(getApplicationContext(), new SomethingMethods.Connection() {
                @Override
                public void isConnected() {
                    dbChats.child(chat.id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.child("messages").getRef().setValue(messageList);
                            etSend.setText("");

                            /*
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                child.getRef().orderByChild("id").equalTo(getIntent().getExtras().getString("id")).getRef().child("messages").setValue(messageList);
                            }

                             */
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            });

        }
    }
    // открытие меню чата
    public void onChatMenuOpen(View view) {
    }
    public void onAddImage(View view) {
    }
}