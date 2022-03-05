package com.example.smartarzamas.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.IconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatListAdapter extends FirebaseAdapter<Chat, ChatListAdapter.ChatHolder> {

    private Map<String, Bitmap> savedIcons = new HashMap<>();

    public ChatListAdapter(Context context, User user, boolean isAdmin, ArrayList<Chat> items, OnStateClickListener<Chat> onItemClickListener) {
        super(context, user, isAdmin, items, onItemClickListener);
    }

    @Override
    protected ChatHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false));
    }


    public class ChatHolder extends FirebaseHolder<Chat> {

        private final ImageButton btMenu;
        private final ProgressBar progressImage;
        private final TextView tvName;
        private final TextView tvCategory;
        private final IconView ivIcon;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            ivIcon = itemView.findViewById(R.id.chat_icon);
            progressImage = itemView.findViewById(R.id.progress);
            btMenu = itemView.findViewById(R.id.bt_item_menu);
        }

        @Override
        public void bind(int position) {
            item = getItem(position);
            ivIcon.setVisibility(View.GONE);
            progressImage.setVisibility(View.VISIBLE);
            tvName.setText(item.name);
            tvCategory.setText(item.category);
            btMenu.setVisibility(View.GONE);


            Chat.getChatById(item.id, new OnGetDataListener<Chat>() {
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

        @Override
        public void bindAdmin(int position) {
            btMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            btMenu.setVisibility(View.VISIBLE);
        }
    }
}

