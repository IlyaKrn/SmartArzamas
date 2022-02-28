package com.example.smartarzamas;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.DialogAdminAccountReject;
import com.example.smartarzamas.ui.DialogUserIconChange;
import com.example.smartarzamas.ui.DialogUserNameAndFamilyChange;
import com.example.smartarzamas.ui.OnIconChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AdminUserSettingsActivity extends FirebaseActivity {

    ImageView userIcon;
    TextView userName;
    TextView userFamily;
    TextView userEmail;
    ProgressBar progressBar;
    ValueEventListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_settings);
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
        progressBar = findViewById(R.id.progress);
    }
    // изменение имени и фамилии
    public void onChangeUserNameAndFamily(View view) {
        Utils.isConnected(getApplicationContext(), new Utils.Connection() {
            @Override
            public void isConnected() {
                DialogUserNameAndFamilyChange dialog = new DialogUserNameAndFamilyChange(AdminUserSettingsActivity.this, user);
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
        DialogUserIconChange dialog = new DialogUserIconChange(AdminUserSettingsActivity.this, user);
        dialog.create(R.id.fragmentContainerView);
        dialog.setOnIconChangeListener(new OnIconChangeListener() {
            @Override
            public void onChange(Bitmap bitmap) {
                userIcon.setImageBitmap(bitmap);
            }
        });

    }
    private void updateViewData(){
        if (userIcon.getDrawable() == null){
            userIcon.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        User.getUserById(user.id, new OnGetDataListener<User>() {
            @Override
            public void onGetData(User data) {
                AdminUserSettingsActivity.this.user = data;
                userName.setText(user.name);
                userFamily.setText(user.family);
                userEmail.setText(user.email);
                user.getIconAsync(AdminUserSettingsActivity.this.getApplicationContext(), new OnGetIcon() {
                    @Override
                    public void onLoad(Bitmap bitmap) {
                        userIcon.setImageBitmap(bitmap);
                        userIcon.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onVoidData() {
                Toast.makeText(AdminUserSettingsActivity.this, getString(R.string.data_not_find), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {
                Toast.makeText(AdminUserSettingsActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        dbUsers.removeEventListener(userListener);
        super.onDestroy();
    }

    public void onRejection(View view) {
        DialogAdminAccountReject dialog = new DialogAdminAccountReject(this, user);
        dialog.create(R.id.fragmentContainerView);
    }
}