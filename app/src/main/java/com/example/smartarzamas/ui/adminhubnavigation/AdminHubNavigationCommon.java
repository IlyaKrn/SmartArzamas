package com.example.smartarzamas.ui.adminhubnavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Locate;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public abstract class AdminHubNavigationCommon extends Fragment {
    public static final String LOG_TAG = "AdminHubNavigationLog";
    public static final String ALL_CHATS = "all";
    public static final String MY_CHATS = "my";
    public static final String MAP = "map";

    public static String currentNavigationFragment;
    protected ArrayList<String> category;
    protected static ArrayList<String> DEFAULT_CATEGORY;
    protected String searchString;
    protected View root;

    protected StorageReference firebaseStorage; // хранилище картинок
    protected DatabaseReference dbUsers;  // бд пользователей (Firebase)
    protected DatabaseReference dbChats;  // бд чатов (Firebase)
    protected DatabaseReference dbLocates;  // бд меток (Firebase)
    protected FirebaseAuth auth; // аутентификация
    protected static User user;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init(inflater, container, savedInstanceState);
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        dbUsers = User.getDatabase();
        dbChats = Chat.getDatabase();
        dbLocates = Locate.getDatabase();
        auth = FirebaseAuth.getInstance();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addHubActivityCallback();
    }


    protected abstract void addHubActivityCallback();

    protected abstract void init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public static void setUser(User user) {
        AdminHubNavigationCommon.user = user;
    }
}
