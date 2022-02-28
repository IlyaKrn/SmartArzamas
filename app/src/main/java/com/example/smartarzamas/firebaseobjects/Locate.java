package com.example.smartarzamas.firebaseobjects;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class Locate extends FirebaseObject {

    public static final String LOCATES = "locates";
    public double longitude;
    public double latitude;
    public String description;
    public String category;

    public Locate() {
    }

    public Locate(String name, String id, double longitude, double latitude, String description, String category) {
        super(name, id);
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.category = category;
    }

    public static DatabaseReference getDatabase(){
        return FirebaseDatabase.getInstance().getReference(LOCATES);
    }
    @Override
    protected DatabaseReference getDatabaseChild() {
        return getDatabase();
    }
    public LatLng getLocate(){
        return new LatLng(latitude, longitude);
    }

    public static void getLocateById(String id, OnGetDataListener<Locate> onGetDataListener){
        getDatabase().child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Locate.class) != null) {
                    onGetDataListener.onGetData(snapshot.getValue(Locate.class));
                    Log.d(LOG_TAG, "gotten locate: " + snapshot.getValue(Chat.class).name);
                }
                else {
                    onGetDataListener.onVoidData();
                    Log.d(LOG_TAG, "gotten locate name: " + "null");
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

}
