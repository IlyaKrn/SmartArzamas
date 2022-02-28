package com.example.smartarzamas.firebaseobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.smartarzamas.support.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class FirebaseObject implements Serializable {

    public static final String LOG_TAG = "FirebaseObject";
    public static final String ICONS_REF = "icons/";
    protected static final String DEFAULT_ICON_REF = ICONS_REF + "default_icon.png";
    public static final int ICON_QUALITY = 100;
    public String name;
    public String id;
    public String iconRef;

    protected static Map<String, ValueEventListener> databaseListeners = new HashMap<>();

    public FirebaseObject(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public FirebaseObject() {
    }

    public static DatabaseReference getDatabase() {
        return null;
    }
    protected abstract DatabaseReference getDatabaseChild();

    public void getIconAsync(Context context, OnGetIcon onGetIcon){
        Utils.isConnected(context, new Utils.Connection() {
            @Override
            public void isConnected() {
                if (iconRef == null){
                    getDefaultIcon(new OnGetIcon() {
                        @Override
                        public void onLoad(Bitmap bitmap) {
                            onGetIcon.onLoad(bitmap);
                        }
                    });
                }
                else {
                    FirebaseStorage.getInstance().getReference().child(iconRef).getBytes(1024 * 1024 * 1024).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            try {
                                if (task.getResult() != null) {
                                    Bitmap icon = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                    onGetIcon.onLoad(icon);
                                } else {
                                    getDefaultIcon(new OnGetIcon() {
                                        @Override
                                        public void onLoad(Bitmap bitmap) {
                                            onGetIcon.onLoad(bitmap);
                                        }
                                    });
                                }
                            } catch (Exception e){
                                getDefaultIcon(new OnGetIcon() {
                                    @Override
                                    public void onLoad(Bitmap bitmap) {
                                        onGetIcon.onLoad(bitmap);
                                    }
                                });
                            }

                        }
                    });
                }
            }
        });
    }
    public void setIconAsync(Context context,Bitmap bitmap, OnSetIcon onSetIcon){
        Utils.isConnected(context, new Utils.Connection() {
            @Override
            public void isConnected() {
                String path = ICONS_REF + getDatabaseChild().getKey() + "/" + FirebaseObject.this.id;
                final StorageReference uploadRef = FirebaseStorage.getInstance().getReference(path);
                uploadRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        UploadTask uploadTask = uploadRef.putBytes(getBytes(Utils.compressBitmapToIcon(bitmap, ICON_QUALITY)));
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                return uploadRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    iconRef = path;
                                    getDatabaseChild().child(FirebaseObject.this.id).child("iconRef").setValue(iconRef).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                onSetIcon.onSet(iconRef, Utils.compressBitmapToIcon(bitmap, ICON_QUALITY));
                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }
                });
            }
        });
    }

    protected final byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    protected final void getDefaultIcon(OnGetIcon onGetIcon){
        FirebaseStorage.getInstance().getReference().child(DEFAULT_ICON_REF).getBytes(1024 * 1024 * 1024).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if (task.getResult() != null) {
                    Bitmap icon = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                    onGetIcon.onLoad(icon);
                }
            }
        });
    }

    public void delete(OnDelete onDelete){
        getDatabase().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onDelete.onDelete();
            }
        });
    }
}
