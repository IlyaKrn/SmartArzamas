package com.example.smartarzamas.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetUser;
import com.example.smartarzamas.firebaseobjects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageHolder> {

    private final OnStateClickListener onClickListener;
    ArrayList<Message> messages;
    Context context;
    User user;
    private Map<String, Bitmap> savedIcons = new HashMap<>();

    public MessageListAdapter(Context context, ArrayList<Message> messages,/* ArrayList<User>  userList,*/ User user, OnStateClickListener onClickListener) {
        this.context = context;
        this.messages = messages;
        this.onClickListener = onClickListener;
        this.user = user;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_message_list, parent, false);

        MessageHolder holder = new MessageHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onClickListener.onStateClick(position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface OnStateClickListener{
        void onStateClick(int messagePosition);
    }

    class MessageHolder extends RecyclerView.ViewHolder{

        ProgressBar progressImage;
        TextView tvMessage;
        TextView tvName;
        ImageView ivIcon;
        TextView tvDate;
        View itemBody;
        User u;
        Message m;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = this.itemView.findViewById(R.id.tv_message);
            tvName = this.itemView.findViewById(R.id.tv_user_name);
            ivIcon = this.itemView.findViewById(R.id.user_icon);
            tvDate = this.itemView.findViewById(R.id.tv_date);
            itemBody = itemView.findViewById(R.id.message_body);
            progressImage = itemView.findViewById(R.id.progress);
        }

        public void bind(int listIndex){
            m = messages.get(listIndex);
            tvMessage.setText(m.message);
            if (m.userId != null) {
                ivIcon.setVisibility(View.GONE);
                progressImage.setVisibility(View.VISIBLE);
                User.getUserById(m.userId, new OnGetUser() {
                    @Override
                    public void onGet(User user) {
                        u = user;
                        if (user.id.equals(m.userId))
                            tvName.setText(user.name);
                        if (savedIcons.get(user.id) != null){
                            if (user.id.equals(m.userId)) {
                                ivIcon.setImageBitmap(savedIcons.get(user.id));
                                ivIcon.setVisibility(View.VISIBLE);
                                progressImage.setVisibility(View.GONE);
                            }
                        }
                        else {
                            user.getIconAsync(context, new OnGetIcon() {
                                @Override
                                public void onLoad(Bitmap bitmap) {
                                    savedIcons.put(user.id, bitmap);
                                    if (user.id.equals(m.userId)) {
                                        ivIcon.setImageBitmap(bitmap);
                                        ivIcon.setVisibility(View.VISIBLE);
                                        progressImage.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }

                    }
                });
            }

        }

    }
}
