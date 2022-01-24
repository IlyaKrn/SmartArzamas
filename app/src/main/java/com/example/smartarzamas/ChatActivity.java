package com.example.smartarzamas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.adapters.MessageListAdapter;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.SomethingMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public static DatabaseReference dbUsers; // бд пользователей (Firebase)
    public static DatabaseReference dbChats; // бд чатов (Firebase)
    ArrayList<User> userList;
    TextView tvName;
    RecyclerView rvMessages;
    EditText etSend;
    ArrayList<Message> messageList;
    MessageListAdapter adapter;
    final String NULL_MESSAGE = "";
    LinearLayoutManager layoutManager;
    static ArrayList<Integer> selectedIds;
    String chatId;
    ArrayList<Bitmap> imagesForSend;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        getChatData();
        getUserList();
    }

    // инициализация
    void init(){
        userList = new ArrayList<>();
        dbUsers = User.getDatabase();
        imagesForSend = new ArrayList<>();
        tvName = (TextView)findViewById(R.id.tvName);
        etSend = (EditText)findViewById(R.id.etSend);
        chatId = getIntent().getExtras().getString("chat_id");
        selectedIds = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        user = (User) getIntent().getSerializableExtra("user");
        dbChats = Chat.getDatabase();
        messageList = new ArrayList<>();
        rvMessages = findViewById(R.id.rv_messages);
        rvMessages.setLayoutManager(layoutManager);
        adapter = new MessageListAdapter(messageList, userList, user, new MessageListAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(int messagePosition) {

            }
        });
        rvMessages.setAdapter(adapter);
    }
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
    // получение информации о чате
    void getChatData(){
        SomethingMethods.isConnected(getApplicationContext(), new SomethingMethods.Connection() {
            @Override
            public void isConnected() {
                dbChats.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String id = getIntent().getExtras().getString("chat_id");
                            Chat c = ds.getValue(Chat.class);
                            assert c != null;
                            if (c.id.equals(id)) {
                                tvName.setText(c.name);
                                if (messageList.size() > 0) messageList.clear();
                                for (int i = 0; i < c.messages.size(); i++) {
                                    messageList.add(c.messages.get(i));
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        rvMessages.scrollToPosition(messageList.size()-1);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
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
            messageList.add(new Message(massege, user.email, imagesForSend));

            SomethingMethods.isConnected(getApplicationContext(), new SomethingMethods.Connection() {
                @Override
                public void isConnected() {
                    dbChats.orderByChild("id").equalTo(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                child.getRef().orderByChild("id").equalTo(getIntent().getExtras().getString("id")).getRef().child("messages").setValue(messageList);
                                etSend.setText("");
                            }
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