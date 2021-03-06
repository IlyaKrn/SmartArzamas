package com.example.smartarzamas.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.TypedValue;
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

import com.example.smartarzamas.AdminChatActivity;
import com.example.smartarzamas.FirebaseActivity;
import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnSetDataListener;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.IconView;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.adminhubnavigation.adminallchats.AdminAllChatsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatListAdapter extends FirebaseAdapter<Chat, ChatListAdapter.ChatHolder> {

    private Map<String, Bitmap> savedIcons = new HashMap<>(); // кэш картинок

    // конструктор
    public ChatListAdapter(Context context, User user, boolean isAdmin, ArrayList<Chat> items, OnStateClickListener<Chat> onItemClickListener) {
        super(context, user, isAdmin, items, onItemClickListener);
    }

    // создание холдера
    @Override
    protected ChatHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false));
    }


    // холдер
    public class ChatHolder extends FirebaseHolder<Chat> {

        // элементы разметки
        private final ImageButton btMenu;
        private final ProgressBar progressImage;
        private final TextView tvName;
        private final TextView tvCategory;
        private final IconView ivIcon;

        // конструктор
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            ivIcon = itemView.findViewById(R.id.chat_icon);
            progressImage = itemView.findViewById(R.id.progress);
            btMenu = itemView.findViewById(R.id.bt_item_menu);
        }

        // обновление холдера
        @Override
        public void bind(int position) {
            item = getItem(position); // получение элемента
            // установка данных
            ivIcon.setVisibility(View.GONE);
            progressImage.setVisibility(View.VISIBLE);
            tvName.setText(item.name);
            tvCategory.setText(item.category);
            btMenu.setVisibility(View.GONE);


            itemView.setBackgroundColor(Utils.getColorFromTheme(context, R.attr.recyclerView_default_background));
            if (item.banned){
                itemView.setBackgroundColor(Utils.getColorFromTheme(context, R.attr.recyclerView_red_background));
            }

            // установка картинки из кэша или загрузка из Firebase
            Chat.getChatById(context, item.id, new OnGetDataListener<Chat>() {
                @Override
                public void onGetData(Chat data) {
                    if (savedIcons.get(item.id) != null){
                        if (data.id.equals(item.id)) {
                            ivIcon.setImageBitmap(savedIcons.get(item.id));
                            ivIcon.setVisibility(View.VISIBLE);
                            progressImage.setVisibility(View.GONE);
                        }
                    }
                    else {
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

        // если пользоватнль - администратор
        @Override
        public void bindAdmin(int position) {
            // кнопка меню
            btMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.inflate(R.menu.popup_menu_chat_list_item);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.ban:
                                        item.banned = true;
                                        item.setNewData(context, item, new OnSetDataListener<Chat>() {
                                            @Override
                                            public void onSetData(Chat data) {
                                                Toast.makeText(context, "Чат " + data.name + " заблокирован", Toast.LENGTH_SHORT).show();
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
                                        item.setNewData(context, item, new OnSetDataListener<Chat>() {
                                            @Override
                                            public void onSetData(Chat data) {
                                                Toast.makeText(context, "Чат " + data.name + " разблокирован", Toast.LENGTH_SHORT).show();
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

