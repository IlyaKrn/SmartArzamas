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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.User;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;

public class DialogUserIconChange extends Dialog{

    private Button change, cancel, select;
    private ImageView icon;
    private Bitmap bitmap;
    private Bitmap currentIcon;
    private User user;

    public DialogUserIconChange(AppCompatActivity activity, User user) {
        super(activity);
        this.user = user;
    }
    public DialogUserIconChange(Fragment fragment, User user) {
        super(fragment);
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_user_icon_change, container, false);
        change = rootView.findViewById(R.id.bt_change);
        cancel = rootView.findViewById(R.id.bt_cancel);
        select = rootView.findViewById(R.id.bt_select);
        icon = rootView.findViewById(R.id.icon);
        user.getIconAsync(context, new OnGetIcon() {
            @Override
            public void onLoad(Bitmap bitmap) {
                currentIcon = bitmap;
                icon.setImageBitmap(currentIcon);
            }
        });


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseStorage.getInstance().getReference().putBytes(toBytes(bitmap));
                destroy();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(intent, 1);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null && requestCode == 1){
            change.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.change_view_barrier).setVisibility(View.VISIBLE);
            icon.setImageURI(data.getData());
            bitmap = ((BitmapDrawable) icon.getDrawable()).getBitmap();
        }
    }
    private byte[] toBytes(Bitmap b){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
