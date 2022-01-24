package com.example.smartarzamas.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Chat;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder> {

    private final OnStateClickListener onClickListener;
    ArrayList<Chat> chats;

    public ChatListAdapter(ArrayList<Chat> chats, OnStateClickListener onClickListener) {
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

        TextView tvName;
        TextView tvCategory;
        ImageView bmIcon;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            bmIcon = itemView.findViewById(R.id.chat_icon);
        }

        void bind(int listIndex){
            tvName.setText(chats.get(listIndex).name);
            tvCategory.setText(chats.get(listIndex).category);
            if (chats.get(listIndex).icon != null)
                bmIcon.setImageBitmap(chats.get(listIndex).icon);

        }
    }
}

