package com.example.smartarzamas.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder> {

    private final OnStateClickListener onClickListener;
    private final Context context;
    ArrayList<Chat> chats;

    private Map<String, Bitmap> savedIcons = new HashMap<>();


    public ChatListAdapter(Context context, ArrayList<Chat> chats, OnStateClickListener onClickListener) {
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

        ProgressBar progressImage;
        TextView tvName;
        TextView tvCategory;
        ImageView ivIcon;
        Chat c;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            ivIcon = itemView.findViewById(R.id.chat_icon);
            progressImage = itemView.findViewById(R.id.progress);
        }

        void bind(int listIndex){
            c = chats.get(listIndex);
            ivIcon.setVisibility(View.GONE);
            progressImage.setVisibility(View.VISIBLE);
            tvName.setText(c.name);
            tvCategory.setText(c.category);
            if (savedIcons.get(c.id) != null){
                ivIcon.setImageBitmap(savedIcons.get(c.id));
                ivIcon.setVisibility(View.VISIBLE);
                progressImage.setVisibility(View.GONE);
            }
            else {
                chats.get(listIndex).getIconAsync(context, new OnGetIcon() {
                    @Override
                    public void onLoad(Bitmap bitmap) {
                        savedIcons.put(c.id, bitmap);
                        ivIcon.setImageBitmap(bitmap);
                        ivIcon.setVisibility(View.VISIBLE);
                        progressImage.setVisibility(View.GONE);
                    }
                });
            }

        }
    }
}

