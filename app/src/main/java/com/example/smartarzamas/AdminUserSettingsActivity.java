package com.example.smartarzamas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smartarzamas.firebaseobjects.OnDeleteDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.DialogConfirm;
import com.example.smartarzamas.ui.DialogUserIconChange;
import com.example.smartarzamas.ui.DialogUserNameAndFamilyChange;
import com.example.smartarzamas.ui.OnConfirmListener;
import com.example.smartarzamas.ui.OnIconChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AdminUserSettingsActivity extends FirebaseActivity {

    ImageView userIcon;
    TextView userName;
    TextView userFamily;
    TextView userEmail;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_settings);
        init();
        updateViewData();
        user.addUserListener("7", new OnGetDataListener<User>() {
            @Override
            public void onGetData(User data) {
                AdminUserSettingsActivity.this.user = data;
                updateViewData();
            }

            @Override
            public void onVoidData() {
                Toast.makeText(getApplicationContext(), getString(R.string.data_not_find), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminUserSettingsActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(), getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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
        DialogConfirm dialog = new DialogConfirm(this, getString(R.string.delete_account), getString(R.string.delete), getString(R.string.realy_delete_account), new OnConfirmListener() {
            @Override
            public void onConfirm(DialogConfirm d) {
                d.freeze();
                user.removeFromDatabase(new OnDeleteDataListener() {
                    @Override
                    public void onDataDelete(DatabaseReference deleteRef){
                        auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    user.removeFromDatabase(new OnDeleteDataListener() {
                                        @Override
                                        public void onDataDelete(DatabaseReference deleteRef) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.delete_account_succesful), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AdminUserSettingsActivity.this, AuthActivity.class);
                                            startActivity(intent);
                                            finish();
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
                                else {
                                    Toast.makeText(AdminUserSettingsActivity.this, getString(R.string.delete_account_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onNoConnection() {

                    }

                    @Override
                    public void onCanceled() {

                    }
                });
            }

            @Override
            public void onCancel(DialogConfirm d) {
                d.destroy();
            }
        });
        dialog.create(R.id.fragmentContainerView);
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
    protected void onDestroy() {
        user.removeObjectDataListener("7");
        super.onDestroy();
    }

    public void onRejection(View view) {
        DialogConfirm dialog = new DialogConfirm(this, getString(R.string.rejection), getString(R.string.reject), getString(R.string.realy_reject), new OnConfirmListener() {
            @Override
            public void onConfirm(DialogConfirm d) {
                d.freeze();
                User.getDatabase().child(user.id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.child("isAdmin").getRef().setValue(false);
                        Intent intent = new Intent(AdminUserSettingsActivity.this, AuthActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancel(DialogConfirm d) {
                d.destroy();
            }
        });
        dialog.create(R.id.fragmentContainerView);
    }
}