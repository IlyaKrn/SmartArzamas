package com.example.smartarzamas.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnSetDataListener;
import com.example.smartarzamas.firebaseobjects.OnUpdateUser;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.IconView;
import com.example.smartarzamas.support.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListAdapter extends FirebaseAdapter<User, UserListAdapter.UserHolder> {

    private Map<String, Bitmap> savedIcons = new HashMap<>();

    public UserListAdapter(Context context, User user, boolean isAdmin, ArrayList<User> items, OnStateClickListener<User> onItemClickListener) {
        super(context, user, isAdmin, items, onItemClickListener);
    }


    @Override
    protected UserHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false));
    }

    public class UserHolder extends FirebaseHolder<User>{

        private final ImageButton btMenu;
        private final ProgressBar progressImage;
        private final TextView tvName;
        private final TextView tvEmail;
        private final IconView ivIcon;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            btMenu = itemView.findViewById(R.id.bt_item_menu);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            ivIcon = itemView.findViewById(R.id.user_icon);
            progressImage = itemView.findViewById(R.id.progress);
        }

        @Override
        public void bind(int position) {
            item = getItem(position);
            ivIcon.setVisibility(View.GONE);
            progressImage.setVisibility(View.VISIBLE);
            btMenu.setVisibility(View.GONE);
            tvName.setText(item.name);
            tvEmail.setText(item.email);

            itemView.setBackgroundColor(Utils.getColorFromTheme(context, R.attr.recyclerView_default_background));
            if (item.banned){
                itemView.setBackgroundColor(Utils.getColorFromTheme(context, R.attr.recyclerView_red_background));
            }
            if (item.isAdmin){
                itemView.setBackgroundColor(Utils.getColorFromTheme(context, R.attr.recyclerView_gray_background));
            }

            User.getUserById(context, item.id, new OnGetDataListener<User>() {
                @Override
                public void onGetData(User data) {
                    if (savedIcons.get(item.id) != null) {
                        if (data.id.equals(item.id)) {
                            ivIcon.setImageBitmap(savedIcons.get(item.id));
                            ivIcon.setVisibility(View.VISIBLE);
                            progressImage.setVisibility(View.GONE);
                        }
                    } else {
                        data.getIconAsync(context, new OnGetIcon() {
                            @Override
                            public void onLoad(Bitmap bitmap) {
                                savedIcons.put(data.id, bitmap);
                                if (data.id.equals(item.id)) {
                                    ivIcon.setImageBitmap(bitmap);
                                    ivIcon.setVisibility(View.VISIBLE);
                                    progressImage.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onVoidData() {

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
        public void bindAdmin(int position) {
            if (!user.email.equals(item.email)) {
                btMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.inflate(R.menu.popup_menu_user_list_item);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.set_admin:
                                        item.isAdmin = true;
                                        item.setNewData(context, item, new OnSetDataListener<User>() {
                                            @Override
                                            public void onSetData(User data) {
                                                Toast.makeText(context, "Пользователь " + user.email + " стал администратором", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onNoConnection() {

                                            }

                                            @Override
                                            public void onCanceled() {

                                            }
                                        });
                                        break;
                                    case R.id.remove_admin:
                                        item.isAdmin = false;
                                        item.setNewData(context, item, new OnSetDataListener<User>() {
                                            @Override
                                            public void onSetData(User data) {
                                                Toast.makeText(context, "Пользователь " + user.email + " стал пользователем", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onNoConnection() {

                                            }

                                            @Override
                                            public void onCanceled() {

                                            }
                                        });
                                        break;
                                    case R.id.ban:
                                        item.banned = true;
                                        item.setNewData(context, item, new OnSetDataListener<User>() {
                                            @Override
                                            public void onSetData(User data) {
                                                Toast.makeText(context, "Пользователь " + user.email + " заблокирован", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onNoConnection() {

                                            }

                                            @Override
                                            public void onCanceled() {

                                            }
                                        });
                                        break;
                                    case R.id.unban:
                                        item.banned = false;
                                        item.setNewData(context, item, new OnSetDataListener<User>() {
                                            @Override
                                            public void onSetData(User data) {
                                                Toast.makeText(context, "Пользователь " + user.email + " разблокирован", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onNoConnection() {

                                            }

                                            @Override
                                            public void onCanceled() {

                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });
                btMenu.setVisibility(View.VISIBLE);
            }
        }
    }
}
