package com.example.smartarzamas.firebaseobjects;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat extends FirebaseObject {

    public static final String CHAT = "chat";
    public String description;
    public ArrayList<Message> messages;
    public ArrayList<User> blackList;
    public String category;
    public ArrayList<String> membersEmailList;

    public Chat() {
    }

    public Chat(String name, String description, String id, ArrayList<Message> messages, String category, User user) {
        super(name, id);
        this.description = description;
        this.messages = messages;
        this.category = category;
        blackList = new ArrayList<>();
        membersEmailList = new ArrayList<>();
        membersEmailList.add(user.email);
    }


    public static DatabaseReference getDatabase(){
        return FirebaseDatabase.getInstance().getReference("chats");
    }

    @Override
    protected DatabaseReference getDatabaseChild() {
        return getDatabase();
    }

    public void addMember(User user){
        membersEmailList.add(user.email);
    }
    public void removeMember(User user){
        for (int i = 0; i < membersEmailList.size(); i++){
            if (membersEmailList.get(i).equals(user.email)){
                membersEmailList.remove(i);
                break;
            }
        }
    }
    public boolean isMember(User user){
        if (membersEmailList != null) {
            for (int i = 0; i < membersEmailList.size(); i++) {
                if (membersEmailList.get(i).equals(user.email)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static void getChatById(String id, OnGetChat onGetChat){
        getDatabase().child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onGetChat.onGet(snapshot.getValue(Chat.class));
                if (snapshot.getValue(Chat.class) != null)
                    Log.d(LOG_TAG, "gotten chat: " + snapshot.getValue(Chat.class).name);
                else {
                    Log.d(LOG_TAG, "gotten chat name: " + "null");
                }
                getDatabase().child(id).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(LOG_TAG, "firebase error: " + error.getDetails());
                getDatabase().child(id).removeEventListener(this);
            }
        });
    }

}
