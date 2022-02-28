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
    public static void addLocateListListener(String key, OnGetListDataListener<Locate> onGetDataListener){
        getDatabase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseListeners.put(key, this);
                if (snapshot.getValue(Chat.class) != null) {
                    ArrayList<Locate> locates = new ArrayList<>();
                    for (DataSnapshot s : snapshot.getChildren()){
                        Locate l = s.getValue(Locate.class);
                        assert l != null;
                        locates.add(l);
                    }
                    onGetDataListener.onGetData(locates);
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
    public void addLocateListener(String key, OnGetDataListener<Locate> onGetDataListener){
        getDatabase().child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseObjectListeners.put(key, this);
                if (snapshot.getValue(Chat.class) != null) {
                    Locate locate = snapshot.getValue(Locate.class);
                    onGetDataListener.onGetData(locate);
                }
                else {
                    onGetDataListener.onVoidData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseObjectListeners.put(key, this);
                Log.e(LOG_TAG, "firebase error: " + error.getDetails());
                onGetDataListener.onCanceled();
            }
        });
    }
    public static void removeDataListener(String key){
        getDatabase().removeEventListener(databaseListeners.get(key));
    }
    public void removeObjectDataListener(String key){
        getDatabase().child(id).removeEventListener(databaseObjectListeners.get(key));
    }
}
