package com.example.smartarzamas.ui.hubnavigation;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Locate;
import com.example.smartarzamas.firebaseobjects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public abstract class HubNavigationCommon extends Fragment {

    public static final String LOG_TAG = "HubNavigationLog";
    public static final String ALL_CHATS = "all";
    public static final String MY_CHATS = "my";
    public static final String MAP = "map";

    public static String currentNavigationFragment;
    protected ArrayList<String> category;
    protected static ArrayList<String> DEFAULT_CATEGORY;
    protected String searchString;

    protected StorageReference firebaseStorage; // хранилище картинок
    protected DatabaseReference dbUsers;  // бд пользователей (Firebase)
    protected DatabaseReference dbChats;  // бд чатов (Firebase)
    protected DatabaseReference dbLocates;  // бд меток (Firebase)
    protected FirebaseAuth auth; // аутентификация


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        category = DEFAULT_CATEGORY;
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

    public static void setDefaultCategory(ArrayList<String> defaultCategory) {
        DEFAULT_CATEGORY = defaultCategory;
    }
}
