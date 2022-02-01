package com.example.smartarzamas.firebaseobjects;

import android.graphics.Bitmap;
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

    public double longitude;
    public double latitude;
    public Bitmap image;
    public String description;
    public ArrayList<String> tags;

    public Locate() {
    }

    public Locate(String name, double longitude, double latitude, Bitmap image, String description, ArrayList<String> tags, String date) {
        super(name, null);
        this.longitude = longitude;
        this.latitude = latitude;
        this.image = image;
        this.description = description;
        this.tags = tags;
    }

    public static DatabaseReference getDatabase(){
        return FirebaseDatabase.getInstance().getReference("locates");
    }

    @Override
    public void getIconAsync(OnLoadBitmap onLoadBitmap) {

    }

    public LatLng getLocate(){
        return new LatLng(latitude, longitude);
    }

    public boolean isThisLocateTags(ArrayList<String> tags){
        if (this.tags != null && this.tags.size() > 0 && tags.size() > 0) {
            Log.e(null, Arrays.toString(this.tags.toArray()) + " " + Arrays.toString(tags.toArray()));
            for (String tagA : this.tags) {
                for (String tagB : tags) {
                    Log.e(null, tagA + " " + tagB);
                    if (tagA.equals(tagB)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static void getLocateById(String id, OnGetLocate onGetLocate){
        getDatabase().child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onGetLocate.onGet(snapshot.getValue(Locate.class));
                if (snapshot.getValue(Locate.class) != null)
                    Log.e(LOG_TAG, "gotten locate name: " + snapshot.getValue(Locate.class).name);
                else {
                    Log.e(LOG_TAG, "gotten locate name: " + "null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(LOG_TAG, "firebase error: " + error.getDetails());
            }
        });
    }

}
