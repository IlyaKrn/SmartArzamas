package com.example.smartarzamas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.SomethingMethods;
import com.example.smartarzamas.ui.DialogUserIconChange;
import com.example.smartarzamas.ui.DialogUserNameAndFamilyChange;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSettingsActivity extends AppCompatActivity {

    public static final String TAG = "UserSettingsActivity";
    final static String USER = "users"; // пользователи в бд
    public static DatabaseReference dbUsers; // бд пользователей (Firebase)
    FirebaseUser currentUser;
    ImageView userIcon;
    TextView userName;
    TextView userFamily;
    TextView userEmail;
    String parentTag;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        init();
    }
    // инициализация
    void init(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userIcon = findViewById(R.id.user_icon);
        userName = findViewById(R.id.user_name);
        userFamily = findViewById(R.id.user_family);
        userEmail = findViewById(R.id.user_email);
        dbUsers = FirebaseDatabase.getInstance().getReference(USER);
        user = (User) getIntent().getSerializableExtra("user");
        parentTag = getIntent().getStringExtra("parent_tag");
        Log.e(TAG, "Parent Tag is:" + parentTag);
        userName.setText(user.name);
        userFamily.setText(" " + user.family);
        userEmail.setText(user.email);
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
  /*      Intent intent;
        switch (parentTag){
            case MapActivity.TAG:
                intent = new Intent(UserSettingsActivity.this, MapActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
                break;
            case ChatListActivity.TAG:
                intent = new Intent(UserSettingsActivity.this, ChatListActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
                break;
        }
*/
    }

    public void onDeleteAccount(View view) {
        /*
        String deliteEmail = currentUser.getEmail();
        Toast.makeText(getApplicationContext(), deliteEmail, Toast.LENGTH_SHORT).show();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();



            }
        });
         */
    }

    public void onChangeUserIcon(View view) {
        DialogUserIconChange dialog = new DialogUserIconChange(UserSettingsActivity.this, user);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, dialog).commit();

    }
}