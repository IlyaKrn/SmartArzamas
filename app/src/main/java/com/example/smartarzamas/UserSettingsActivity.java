package com.example.smartarzamas;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetUser;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.SomethingMethods;
import com.example.smartarzamas.ui.DialogUserIconChange;
import com.example.smartarzamas.ui.DialogUserNameAndFamilyChange;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSettingsActivity extends FirebaseActivity {

    ImageView userIcon;
    TextView userName;
    TextView userFamily;
    TextView userEmail;
    ValueEventListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        init();
        updateViewData();
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateViewData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dbUsers.child(user.id).addValueEventListener(userListener);
    }
    // инициализация
    void init(){
        userIcon = findViewById(R.id.user_icon);
        userName = findViewById(R.id.user_name);
        userFamily = findViewById(R.id.user_family);
        userEmail = findViewById(R.id.user_email);
    }
    // изменение имени и фамилии
    public void onChangeUserNameAndFamily(View view) {
        SomethingMethods.isConnected(getApplicationContext(), new SomethingMethods.Connection() {
            @Override
            public void isConnected() {
                DialogUserNameAndFamilyChange dialog = new DialogUserNameAndFamilyChange(UserSettingsActivity.this, user);
                dialog.create(R.id.fragmentContainerView);
            }
        });
    }
    // закрытие активности
    public void onCloseInfo(View view) {
        finish();
    }

    public void onDeleteAccount(View view) {

    }

    public void onChangeUserIcon(View view) {
        DialogUserIconChange dialog = new DialogUserIconChange(UserSettingsActivity.this, user);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, dialog).commit();

    }
    private void updateViewData(){
        User.getUserById(user.id, new OnGetUser() {
            @Override
            public void onGet(User user) {
                UserSettingsActivity.this.user = user;
                userName.setText(user.name);
                userFamily.setText(" " + user.family);
                userEmail.setText(user.email);
                user.getIconAsync(UserSettingsActivity.this.getApplicationContext(), new OnGetIcon() {
                    @Override
                    public void onLoad(Bitmap bitmap) {
                        userIcon.setImageBitmap(bitmap);
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        dbUsers.removeEventListener(userListener);
        super.onDestroy();
    }
}