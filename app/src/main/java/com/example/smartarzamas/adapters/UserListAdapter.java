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
import androidx.fragment.app.FragmentActivity;

import com.example.smartarzamas.FirebaseActivity;
import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnUpdateUser;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.IconView;
import com.example.smartarzamas.ui.DialogConfirm;
import com.google.android.gms.common.internal.DialogRedirect;

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
        private final IconView ivIcon;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            btMenu = itemView.findViewById(R.id.bt_item_menu);
            tvName = itemView.findViewById(R.id.tv_user_name);
            ivIcon = itemView.findViewById(R.id.user_icon);
            progressImage = itemView.findViewById(R.id.progress);
        }

        @Override
        public void bind(int position) {
            item = getItem(position);
            btMenu.setVisibility(View.GONE);

            User.getUserById(item.id, new OnGetDataListener<User>() {
                @Override
                public void onGetData(User data) {
                    item = data;
                    if (data.id.equals(item.id))
                        tvName.setText(item.name);
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
            btMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, view);
                    popup.inflate(R.menu.popup_menu_user_list_item);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.view_profile:
                                    break;
                                case R.id.set_admin:
                                    item.isModerator = true;
                                    item.setNewData(item, new OnUpdateUser() {
                                        @Override
                                        public void onUpdate(User user) {
                                            Toast.makeText(context, "Пользователь " + user.email + " стал администратором", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case R.id.remove_admin:
                                    item.isModerator = false;
                                    item.setNewData(item, new OnUpdateUser() {
                                        @Override
                                        public void onUpdate(User user) {
                                            Toast.makeText(context, "Пользователь " + user.email + " стал пользователем", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case R.id.ban:
                                    item.banned = true;
                                    item.setNewData(item, new OnUpdateUser() {
                                        @Override
                                        public void onUpdate(User user) {
                                            Toast.makeText(context, "Пользователь " + user.email + " заблокирован", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case R.id.unban:
                                    item.banned = false;
                                    item.setNewData(item, new OnUpdateUser() {
                                        @Override
                                        public void onUpdate(User user) {
                                            Toast.makeText(context, "Пользователь " + user.email + " разблокирован", Toast.LENGTH_SHORT).show();
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
