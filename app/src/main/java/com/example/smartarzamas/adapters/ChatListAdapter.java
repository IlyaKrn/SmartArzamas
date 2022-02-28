package com.example.smartarzamas.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.IconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder> {

    private final OnStateClickListener onClickListener;
    private final Context context;
    private final ArrayList<Chat> chats;
    private final boolean isAdmin;
    private User user;

    private Map<String, Bitmap> savedIcons = new HashMap<>();


    public ChatListAdapter(Context context, ArrayList<Chat> chats, User user, boolean isAdmin, OnStateClickListener onClickListener) {
        this.isAdmin = isAdmin;
        this.user = user;
        this.context = context;
        this.chats = chats;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_chat_list, parent, false);

        ChatHolder holder = new ChatHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);

        Chat chat = chats.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                // вызываем метод слушателя, передавая ему данные
                onClickListener.onStateClick(chat.id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public interface OnStateClickListener{
        void onStateClick(String chatId);
    }

    class ChatHolder extends RecyclerView.ViewHolder{

        ImageButton btMenu;
        ProgressBar progressImage;
        TextView tvName;
        TextView tvCategory;
        IconView ivIcon;
        Chat c;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            ivIcon = itemView.findViewById(R.id.chat_icon);
            progressImage = itemView.findViewById(R.id.progress);
            btMenu = itemView.findViewById(R.id.bt_item_menu);
        }

        void bind(int listIndex){
            c = chats.get(listIndex);
            ivIcon.setVisibility(View.GONE);
            progressImage.setVisibility(View.VISIBLE);
            tvName.setText(c.name);
            tvCategory.setText(c.category);
            btMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            if (isAdmin){
                btMenu.setVisibility(View.VISIBLE);
            }
            else {
                btMenu.setVisibility(View.GONE);
            }
            Chat.getChatById(c.id, new OnGetDataListener<Chat>() {
                @Override
                public void onGetData(Chat data) {
                    if (savedIcons.get(c.id) != null){
                        if (data.id.equals(c.id)) {
                            ivIcon.setImageBitmap(savedIcons.get(c.id));
                            ivIcon.setVisibility(View.VISIBLE);
                            progressImage.setVisibility(View.GONE);
                        }
                    }
                    else {
                        data.getIconAsync(context, new OnGetIcon() {
                            @Override
                            public void onLoad(Bitmap bitmap) {
                                savedIcons.put(data.id, bitmap);
                                if (data.id.equals(c.id)) {
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
    }
}

