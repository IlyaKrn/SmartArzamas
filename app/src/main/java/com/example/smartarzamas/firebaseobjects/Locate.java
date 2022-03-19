package com.example.smartarzamas.firebaseobjects;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smartarzamas.support.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    public LatLng locate(){
        return new LatLng(latitude, longitude);
    }

    public static void getLocateById(Context context, String id, OnGetDataListener<Locate> onGetDataListener){
        Utils.isConnected(context, new Utils.Connection() {
            @Override
            public void isConnected() {
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

            @Override
            public void isNotConnected() {
                onGetDataListener.onNoConnection();
            }
        });
    }
    public void setNewData(Context context, Locate locate, OnSetDataListener<Locate> onSetDataListener){
        Utils.isConnected(context, new Utils.Connection() {
            @Override
            public void isConnected() {
                getDatabase().child(Locate.this.id).setValue(locate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        onSetDataListener.onSetData(Locate.this);
                    }
                });
            }

            @Override
            public void isNotConnected() {
                onSetDataListener.onNoConnection();
            }
        });

    }
    public static void addLocateListListener(Context context, String key, OnGetListDataListener<Locate> onGetDataListener){
        Utils.isConnected(context, new Utils.Connection() {
            @Override
            public void isConnected() {
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

            @Override
            public void isNotConnected() {
                onGetDataListener.onNoConnection();
            }
        });
    }
    public void addLocateListener(Context context, String key, OnGetDataListener<Locate> onGetDataListener){
        Utils.isConnected(context, new Utils.Connection() {
            @Override
            public void isConnected() {
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

            @Override
            public void isNotConnected() {
                onGetDataListener.onNoConnection();
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
