package com.example.smartarzamas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.smartarzamas.databinding.ActivityHubBinding;
import com.example.smartarzamas.support.Category;
import com.example.smartarzamas.support.EditTextSearch;
import com.example.smartarzamas.ui.DialogAddChat;
import com.example.smartarzamas.ui.DialogAddLocate;
import com.example.smartarzamas.ui.DialogSignOut;
import com.example.smartarzamas.ui.OnDestroyListener;
import com.example.smartarzamas.ui.adminhubnavigation.AdminHubActivityCallback;
import com.example.smartarzamas.ui.adminhubnavigation.adminallchats.AdminAllChatsFragment;
import com.example.smartarzamas.ui.adminhubnavigation.adminallchats.AdminAllChatsFragmentCallback;
import com.example.smartarzamas.ui.adminhubnavigation.adminallusers.AdminAllUsersFragment;
import com.example.smartarzamas.ui.adminhubnavigation.adminallusers.AdminAllUsersFragmentCallback;
import com.example.smartarzamas.ui.adminhubnavigation.adminmap.AdminMapFragment;
import com.example.smartarzamas.ui.adminhubnavigation.adminmap.AdminMapFragmentCallback;
import com.example.smartarzamas.ui.hubnavigation.HubActivityCallback;
import com.example.smartarzamas.ui.hubnavigation.HubNavigationCommon;
import com.example.smartarzamas.ui.hubnavigation.allchats.AllChatsFragment;
import com.example.smartarzamas.ui.hubnavigation.allchats.AllChatsFragmentCallback;
import com.example.smartarzamas.ui.hubnavigation.map.MapFragment;
import com.example.smartarzamas.ui.hubnavigation.map.MapFragmentCallback;
import com.example.smartarzamas.ui.hubnavigation.mychats.MyChatsFragment;
import com.example.smartarzamas.ui.hubnavigation.mychats.MyChatsFragmentCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smartarzamas.databinding.ActivityAdminHubBinding;

import java.util.ArrayList;
import java.util.Objects;


public class AdminHubActivity extends FirebaseActivity {

    private ActivityAdminHubBinding binding;
    ImageButton btMenu, btProfile;
    EditTextSearch etSearch;
    private ArrayList<String> searchingTags = Category.getAllTags();

    private static AdminHubActivityCallback allChatsActivityCallback;
    private static AdminHubActivityCallback myChatsActivityCallback;
    private static AdminHubActivityCallback mapActivityCallback;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchingTags = Category.getAllTags();
        HubNavigationCommon.setUser(user);
        binding = ActivityAdminHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin_hub);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Objects.requireNonNull(getSupportActionBar()).hide();
        btMenu = binding.btMenu;
        btProfile = binding.btProfile;
        etSearch = binding.etSearch;
        setCallbacks();

        etSearch.addTextEditListener(new EditTextSearch.OnTextChangeListener() {
            @Override
            public void onChange(Editable editable) {
                CallbackManager.callOnSearchStringChange(etSearch.getText().toString());

            }
        });
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(AdminHubActivity.this, view);
                popup.inflate(R.menu.popup_menu_map);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            // ямы на дорогах
                            case R.id.pits_on_roads:
                                searchingTags.clear();
                                searchingTags.add(Category.PITS_ON_ROADS);
                                CallbackManager.callOnCategoryChange(searchingTags);
                                break;
                            // лужи
                            case R.id.puddles:
                                searchingTags.clear();
                                searchingTags.add(Category.PUDDLES);
                                CallbackManager.callOnCategoryChange(searchingTags);
                                break;
                            // достопримечатльности
                            case R.id.sights:
                                searchingTags.clear();
                                searchingTags.add(Category.SIGHTS);
                                CallbackManager.callOnCategoryChange(searchingTags);
                                break;
                            // другое
                            case R.id.other:
                                searchingTags.clear();
                                searchingTags.add(Category.OTHER);
                                CallbackManager.callOnCategoryChange(searchingTags);
                                break;
                            // снег
                            case R.id.snow:
                                searchingTags.clear();
                                searchingTags.add(Category.SNOW);
                                CallbackManager.callOnCategoryChange(searchingTags);
                                break;
                            // все
                            case R.id.all:
                                searchingTags.clear();
                                searchingTags = Category.getAllTags();
                                CallbackManager.callOnCategoryChange(searchingTags);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        btProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(AdminHubActivity.this, view);
                popup.inflate(R.menu.popup_profil);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            // настройкм профиля
                            case R.id.profil_settings:
                                Intent intent = new Intent(AdminHubActivity.this, UserSettingsActivity.class);
                                intent.putExtra(USER_INTENT, user);
                                startActivity(intent);
                                break;
                            // выход из аккаунта
                            case R.id.sign_out:
                                DialogSignOut dialog = new DialogSignOut(AdminHubActivity.this);
                                dialog.create(R.id.fragmentContainerView);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        Toast.makeText(this, "refresh", Toast.LENGTH_SHORT).show();
        stopRefreshAnimation();
    }


    private void setCallbacks(){

        AdminAllChatsFragment.setCallback(new AdminAllChatsFragmentCallback() {
            @Override
            public void onCreateChat(Fragment fragment) {
                DialogAddChat dialog = new DialogAddChat(fragment, user);
                dialog.create(R.id.fragmentContainerView);
            }

            @Override
            public void onSearchUpdate(String search) {
                etSearch.setText(search);
            }

            @Override
            public void onCategoryUpdate(ArrayList<String> category) {

            }
        });
        AdminAllUsersFragment.setCallback(new AdminAllUsersFragmentCallback() {
            @Override
            public void onSearchUpdate(String search) {

            }

            @Override
            public void onCategoryUpdate(ArrayList<String> category) {

            }
        });
        AdminMapFragment.setCallback(new AdminMapFragmentCallback() {
            @Override
            public void onSearchUpdate(String search) {
                etSearch.setText(search);
            }

            @Override
            public void onCategoryUpdate(ArrayList<String> category) {

            }

            @Override
            public void onCreateLocate(Fragment fragment, LatLng latLng) {
                DialogAddLocate dialog = new DialogAddLocate(fragment, latLng.longitude, latLng.latitude);
                dialog.create(R.id.fragmentContainerView);
                dialog.setOnDestroyListener(new OnDestroyListener() {
                    @Override
                    public void onDestroy() {
                        Log.e("'lk'lk", ";lk;l");
                    }
                });

            }
        });
    }


    public static void setAdminAllChatsActivityCallback(AdminHubActivityCallback allChatsActivityCallback) {
        AdminHubActivity.allChatsActivityCallback = allChatsActivityCallback;
    }
    public static void setAdminMyChatsActivityCallback(AdminHubActivityCallback myChatsActivityCallback) {
        AdminHubActivity.myChatsActivityCallback = myChatsActivityCallback;
    }
    public static void setAdminMapActivityCallback(AdminHubActivityCallback mapActivityCallback) {
        AdminHubActivity.mapActivityCallback = mapActivityCallback;
    }

    private static class CallbackManager{

        public static void callOnCategoryChange(ArrayList<String> categories){
            if (allChatsActivityCallback != null && HubNavigationCommon.currentNavigationFragment.equals(HubNavigationCommon.ALL_CHATS))
                allChatsActivityCallback.onCategoryChange(categories);
            if (myChatsActivityCallback != null && HubNavigationCommon.currentNavigationFragment.equals(HubNavigationCommon.MY_CHATS))
                myChatsActivityCallback.onCategoryChange(categories);
            if (mapActivityCallback != null && HubNavigationCommon.currentNavigationFragment.equals(HubNavigationCommon.MAP))
                mapActivityCallback.onCategoryChange(categories);
        }

        public static void callOnSearchStringChange(String search){
            if (allChatsActivityCallback != null && HubNavigationCommon.currentNavigationFragment.equals(HubNavigationCommon.ALL_CHATS))
                allChatsActivityCallback.onSearchStringChange(search);
            if (myChatsActivityCallback != null && HubNavigationCommon.currentNavigationFragment.equals(HubNavigationCommon.MY_CHATS))
                myChatsActivityCallback.onSearchStringChange(search);
            if (mapActivityCallback != null && HubNavigationCommon.currentNavigationFragment.equals(HubNavigationCommon.MAP))
                mapActivityCallback.onSearchStringChange(search);
        }

    }
}