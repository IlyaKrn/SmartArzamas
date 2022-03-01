package com.example.smartarzamas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartarzamas.SQLiteDatabase.SQLiteDbManager;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Locate;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class FirebaseActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    // константы для передачи информации между активнотями
    public static final String USER_INTENT = "user";
    public static final String CHAT_ID = "chat_id";
    // тег для логгирования
    public static final String LOG_TAG = "ActivityLog";



    protected User user;  // текущий пользователь
    protected SQLiteDbManager.SQLUser currentUser; // пользователь SQLite
    protected SQLiteDbManager manager;  // бд с пользователями (SQLite)
    protected StorageReference firebaseStorage; // хранилище картинок
    protected DatabaseReference dbUsers;  // бд пользователей (Firebase)
    protected DatabaseReference dbChats;  // бд чатов (Firebase)
    protected DatabaseReference dbLocates;  // бд меток (Firebase)
    protected FirebaseAuth auth; // аутентификация

    protected FragmentContainerView fragmentDefaultContainer;  // контейнер для врагментов
    private SwipeRefreshLayout swipeRefresh;  // обновление данных
    protected Set<FirebaseActivity> activities = new HashSet<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (FirebaseActivity a : activities){
            if (a instanceof AuthActivity || this instanceof AuthActivity)
                a.finish();
        }

        activities.add(this);
        // получение контейнера для фрагментов
        try {
            fragmentDefaultContainer = findViewById(R.id.fragmentContainerView);
        } catch (Exception e){
            // если его нет
            Log.e(LOG_TAG, "fragment container not found: " + e.getMessage());
        }

        if (getIntent().getSerializableExtra(USER_INTENT) != null){
            // получение пользователя из предыдущей активности
            this.user = (User) getIntent().getSerializableExtra(USER_INTENT);
            User.getUserById(user.id, new OnGetDataListener<User>() {
                @Override
                public void onGetData(User data) {
                    FirebaseActivity.this.user = data;
                    Log.i(LOG_TAG, "user updated");
                }

                @Override
                public void onVoidData() {
                    Toast.makeText(getApplicationContext(), getString(R.string.data_not_find), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FirebaseActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onNoConnection() {

                }

                @Override
                public void onCanceled() {
                    Toast.makeText(getApplicationContext(), getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                }
            });
            Log.i(LOG_TAG, "user email is \"" + user.email + "\"");
        }
        else {
            // если пользователь не передавался
            Log.e(LOG_TAG, "intent bundle is null");
        }

        // создание объекта для взаимодействия в SQLite
        manager = new SQLiteDbManager(this);
        Log.i(LOG_TAG, "SQLite database has been received");
        // получение ссылок на базы данных Firebase
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        Log.i(LOG_TAG, "firebase storage reference has been received");
        dbChats = Chat.getDatabase();
        Log.i(LOG_TAG, "firebase database chats reference has been received");
        dbUsers = User.getDatabase();
        Log.i(LOG_TAG, "firebase database users reference has been received");
        dbLocates = Locate.getDatabase();
        Log.i(LOG_TAG, "firebase database locates reference has been received");
        auth = FirebaseAuth.getInstance();
        Log.i(LOG_TAG, "authentication has been received");
        // получение пользователя из SQLite
        currentUser = manager.getCurrentUser();
        // установка слушателя на SQLite database
        manager.setOnDataChangeListener(new SQLiteDbManager.OnDataChangeListener() {
            @Override
            public void onCurrentUserChange(SQLiteDbManager.SQLUser currentUser) {
                FirebaseActivity.this.currentUser = currentUser;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setOnRefreshListener();
    }

    @Override
    public void onRefresh() {

    }
    protected final void stopRefreshAnimation(){
        swipeRefresh.setRefreshing(false);
    }

    private void setOnRefreshListener(){
        swipeRefresh = findViewById(R.id.swipe_refresh_container);
        if (swipeRefresh != null){
            swipeRefresh.setOnRefreshListener(this);
        }
        else {
            Log.e(LOG_TAG, "SwipeRefreshLayout has not been received");
        }
    }

    protected SQLiteDbManager.SQLUser getSQLiteCurrentUser(){
        return manager.getCurrentUser();
    }
}
