package com.example.smartarzamas.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.FirebaseObject;
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnSetDataListener;
import com.example.smartarzamas.firebaseobjects.OnSetIcon;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DialogChatIconChange extends Dialog{

    private Button change, cancel, select;
    private ImageView icon;
    private Bitmap bitmap;
    private Bitmap currentIcon;
    private ProgressBar progressBar;
    private User user;
    private OnIconChangeListener onIconChangeListener;
    private Chat chat;

    public DialogChatIconChange(AppCompatActivity activity, User user, Chat chat) {
        super(activity);
        this.user = user;
        this.chat = chat;
    }
    public DialogChatIconChange(Fragment fragment, User user, Chat chat) {
        super(fragment);
        this.user = user;
        this.chat = chat;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_chat_icon_change, container, false);
        change = rootView.findViewById(R.id.bt_change);
        cancel = rootView.findViewById(R.id.bt_cancel);
        select = rootView.findViewById(R.id.bt_select);
        icon = rootView.findViewById(R.id.icon);
        progressBar = rootView.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        icon.setVisibility(View.GONE);


        chat.getIconAsync(context, new OnGetIcon() {
            @Override
            public void onLoad(Bitmap bitmap) {
                currentIcon = bitmap;
                icon.setImageBitmap(currentIcon);
                progressBar.setVisibility(View.GONE);
                icon.setVisibility(View.VISIBLE);
            }
        });


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freeze();
                progressBar.setVisibility(View.VISIBLE);
                icon.setVisibility(View.GONE);
                chat.setIconAsync(context, bitmap, new OnSetIcon() {
                    @Override
                    public void onSet(String ref, Bitmap bitmap) {
                        Toast.makeText(context, getString(R.string.icon_has_been_changed), Toast.LENGTH_SHORT).show();
                        onIconChangeListener.onChange(bitmap);
                        Chat buffChat = chat;
                        String message = user.name + " " + user.family + " " + getString(R.string.user_change_chat_image);
                        ArrayList<String> icon = new ArrayList<>();
                        icon.add(ref);
                        buffChat.messages.add(new Message(message, null, Chat.getDatabase().push().getKey(), icon));

                        chat.setNewData(getContext(), buffChat, new OnSetDataListener<Chat>() {
                            @Override
                            public void onSetData(Chat data) {
                                destroy();
                            }

                            @Override
                            public void onNoConnection() {

                            }

                            @Override
                            public void onCanceled() {

                            }
                        });
                    }
                });
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroy();
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setOnIconChangeListener(OnIconChangeListener onIconChangeListener) {
        this.onIconChangeListener = onIconChangeListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null && requestCode == 1){
            change.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.change_view_barrier).setVisibility(View.VISIBLE);
            icon.setImageURI(data.getData());
            bitmap = ((BitmapDrawable) icon.getDrawable()).getBitmap();
            icon.setImageBitmap(Utils.compressBitmapToIcon(bitmap, FirebaseObject.ICON_QUALITY));
        }
    }
    private byte[] toBytes(Bitmap b){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
