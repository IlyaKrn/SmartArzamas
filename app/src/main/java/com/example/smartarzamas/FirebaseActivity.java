package com.example.smartarzamas;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartarzamas.SQLiteDatabase.SQLiteDbManager;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Locate;
import com.example.smartarzamas.firebaseobjects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class FirebaseActivity extends AppCompatActivity {

    // константы для передачи информации между активнотями
    public static final String USER_INTENT = "user";
    public static final String CHAT_ID = "chat_id";
    // тег для логгирования
    public static final String LOG_TAG = "ActivityLog";



    User user;  // текущий пользователь
    SQLiteDbManager.SQLUser currentUser; // пользователь SQLite
    SQLiteDbManager manager;  // бд с пользователями (SQLite)
    StorageReference firebaseStorage; // хранилище картинок
    DatabaseReference dbUsers;  // бд пользователей (Firebase)
    DatabaseReference dbChats;  // бд чатов (Firebase)
    DatabaseReference dbLocates;  // бд меток (Firebase)
    FirebaseAuth auth; // аутентификация
    OnRefreshDataListener onRefreshDataListener;

    FragmentContainerView fragmentDefaultContainer;  // контейнер для врагментов
    private SwipeRefreshLayout swipeRefresh;  // обновление данных


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

    protected void setOnRefreshListener(OnRefreshDataListener listener){
        this.onRefreshDataListener = listener;
       // swipeRefresh = findViewById(R.id.swipe_refresh_container);
        if (swipeRefresh != null){
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                    swipeRefresh.setRefreshing(false);
                }
            });
        }
        else {
            Log.e(LOG_TAG, "SwipeRefreshLayout has not been received");
        }
    }
    protected void refresh(){
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (user != null)
                    user = snapshot.getValue(User.class);
                onRefreshDataListener.onRefresh();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        onRefreshDataListener.onRefresh();
    }

    interface OnGetDataListener{
        public void onGetData();
    }
    interface OnRefreshDataListener{
        public void onRefresh();
    }

    SQLiteDbManager.SQLUser getSQLiteCurrentUser(){
        return manager.getCurrentUser();
    }
}
