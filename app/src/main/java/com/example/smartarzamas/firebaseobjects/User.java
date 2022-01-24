package com.example.smartarzamas.firebaseobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;

public class User extends FirebaseObject implements Serializable {

    public String email;
    public String family;
    public String iconRef;
    public String id;
    public boolean isModerator;
    public boolean banned;
    private static final String DEFAULT_ICON_REF = "default_icon";

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
    public void getIconAsync(OnLoad onLoad) {
        FirebaseStorage.getInstance().getReference().child(iconRef).getBytes(1024 * 1024 * 10234).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                Bitmap icon = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                onLoad.onLoad(icon);
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
