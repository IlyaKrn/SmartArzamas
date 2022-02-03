package com.example.smartarzamas.firebaseobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smartarzamas.support.SomethingMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;

public class User extends FirebaseObject implements Serializable {

    public static final String USER = "user";
    public String email;
    public String family;
    public String id;
    public boolean isModerator;
    public boolean banned;

    public User(String email, String name, String family, String iconRef, String id) {
        super(name, id);
        this.email = email;
        this.family = family;
        this.isModerator = false;
        this.banned = false;
        this.id = id;
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
        return FirebaseDatabase.getInstance().getReference("users");
    }
    @Override
    protected DatabaseReference getDatabaseChild() {
        return getDatabase();
    }
    public static void getUserById(String id, OnGetUser onGetUser){
        getDatabase().child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onGetUser.onGet(snapshot.getValue(User.class));
                if (snapshot.getValue(User.class) != null)
                    Log.e(LOG_TAG, "gotten user email: " + snapshot.getValue(User.class).email);
                else {
                    Log.e(LOG_TAG, "gotten user email: " + "null");
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

    public void updateData(OnUpdateUser onUpdate){
        this.getDatabase().child(this.id).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = (String) snapshot.getValue(User.class).name;
                family = (String) snapshot.getValue(User.class).family;
                iconRef = (String) snapshot.getValue(User.class).iconRef;
                onUpdate.onUpdate(User.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    interface OnUpdateUser{
        void onUpdate(User user);
    }


}
