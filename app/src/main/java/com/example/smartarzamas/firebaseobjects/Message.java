package com.example.smartarzamas.firebaseobjects;

import static com.example.smartarzamas.firebaseobjects.FirebaseObject.ICONS_REF;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.smartarzamas.support.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private static final int ICON_QUALITY = 100;
    public String id;
    public String message;
    public String userId;
    public ArrayList<String> imageRefs = new ArrayList<>();

    public Message(String message, String user, String id, ArrayList<String> images) {
        this.id = id;
        this.message = message;
        this.userId = user;
        this.imageRefs = images;
    }

    public Message() {
    }

    public void getIconsAsync(Context context, OnGetIcons onGetIcon){
        if (imageRefs == null){
            onGetIcon.onGet(null);
        }
        else {
            Utils.isConnected(context, new Utils.Connection() {
                @Override
                public void isConnected() {
                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                    final int[] count = {0};
                    for (int i = 0; i < imageRefs.size(); i++) {
                        FirebaseStorage.getInstance().getReference().child(imageRefs.get(i)).getBytes(1024 * 1024 * 1024).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> task) {
                                try {
                                    count[0]++;
                                    if (task.getResult() != null) {
                                        Bitmap b = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                        bitmaps.add(b);
                                        if (count[0] == imageRefs.size()){
                                            onGetIcon.onGet(bitmaps);
                                        }
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        });
                    }

                }
            });
        }

    }


    public void addImageAsync(Context context, Chat chat, Bitmap bitmap, OnSetIcon onSetIcon){
        Utils.isConnected(context, new Utils.Connection() {
            @Override
            public void isConnected() {
                String path = ICONS_REF + "messages/" + chat.id + "_" + chat.name + "/" + Message.this.id + "/" + Chat.getDatabase().push().getKey();
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
                                    String iconRef = path;
                                    if (task.isSuccessful()) {
                                        if (Message.this.imageRefs == null)
                                            Message.this.imageRefs = new ArrayList<>();
                                        Message.this.imageRefs.add(path);
                                        onSetIcon.onSet(iconRef, Utils.compressBitmapToIcon(bitmap, ICON_QUALITY));
                                    }
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
}
