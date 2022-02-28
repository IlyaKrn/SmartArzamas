package com.example.smartarzamas.firebaseobjects;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User extends FirebaseObject {

    public static final String USERS = "users";
    public String email;
    public String family;
    public boolean isModerator;
    public boolean banned;

    public User(String email, String name, String family, String iconRef, String id) {
        super(name, id);
        this.email = email;
        this.family = family;
        this.isModerator = false;
        this.banned = false;
        if (iconRef == null){
            this.iconRef = DEFAULT_ICON_REF;
        }
        else {
            this.iconRef = iconRef;
        }
    }

    public User() {
    }

    public static DatabaseReference getDatabase(){
        return FirebaseDatabase.getInstance().getReference(USERS);
    }
    @Override
    protected DatabaseReference getDatabaseChild() {
        return getDatabase();
    }
    public static void getUserById(String id, OnGetDataListener<User> onGetDataListener){
        getDatabase().child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(User.class) != null) {
                    onGetDataListener.onGetData(snapshot.getValue(User.class));
                    Log.d(LOG_TAG, "gotten user: " + snapshot.getValue(User.class).email);
                }
                else {
                    onGetDataListener.onVoidData();
                    Log.d(LOG_TAG, "gotten user name: " + "null");
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

    public void setNewData(User user, OnUpdateUser onUpdateUser){
        getDatabase().child(this.id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onUpdateUser.onUpdate(User.this);
            }
        });
    }


}
