package com.example.smartarzamas.firebaseobjects;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smartarzamas.support.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat extends FirebaseObject {

    public static final String CHATS = "chats";
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
        return FirebaseDatabase.getInstance().getReference(CHATS);
    }

    @Override
    protected DatabaseReference getDatabaseChild() {
        return getDatabase();
    }

    @Deprecated
    public void addMember(User user){
        membersEmailList.add(user.email);
    }
    @Deprecated
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
    public static void getChatById(String id, OnGetDataListener<Chat> onGetDataListener){
        getDatabase().child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Chat.class) != null) {
                    onGetDataListener.onGetData(snapshot.getValue(Chat.class));
                    Log.d(LOG_TAG, "gotten chat: " + snapshot.getValue(Chat.class).name);
                }
                else {
                    onGetDataListener.onVoidData();
                    Log.d(LOG_TAG, "gotten chat name: " + "null");
                }
                getDatabase().child(id).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(LOG_TAG, "firebase error: " + error.getDetails());
                onGetDataListener.onCanceled();
                getDatabase().child(id).removeEventListener(this);
            }
        });
    }

    public static void getChatList(String key, OnGetListDataCheckListener<Chat> onGetDataListener){
        getDatabase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseListeners.put(key, this);
                if (snapshot.getValue(Chat.class) != null) {
                    ArrayList<Chat> chats = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()){
                        Chat c = s.getValue(Chat.class);
                        assert c != null;
                        chats.add(c);
                    }
                    onGetDataListener.onGetData(chats);
                }
                else {
                    onGetDataListener.onVoidData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseListeners.put(key, this);
                Log.e(LOG_TAG, "firebase error: " + error.getDetails());
                onGetDataListener.onCanceled();
            }
        });
    }

    public void setNewData(Chat chat, OnUpdateChat onUpdateChat){
        getDatabase().child(this.id).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onUpdateChat.onUpdate(Chat.this);
            }
        });
    }

    public static void removeDataListener(String key){
        getDatabase().removeEventListener(databaseListeners.get(key));
    }
}
