package com.example.smartarzamas.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.OnGetUser;
import com.example.smartarzamas.firebaseobjects.User;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageHolder> {

    private final OnStateClickListener onClickListener;
    ArrayList<Message> messages;
   // ArrayList<User> userList;
    Context context;
    User user;

    public MessageListAdapter(Context context, ArrayList<Message> messages,/* ArrayList<User>  userList,*/ User user, OnStateClickListener onClickListener) {
        this.context = context;
        this.messages = messages;
    //    this.userList = userList;
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
/*
    // получение пользователя по логину
    User getUserByEmail(String email){

        for (User u : userList) {
            if (u.email.equals(email))
                return u;
        }
        return null;




    }

*/
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface OnStateClickListener{
        void onStateClick(int messagePosition);
    }








    class MessageHolder extends RecyclerView.ViewHolder{

        TextView tvMessage;
        TextView tvName;
        ImageView bmIcon;
        TextView tvDate;
        View itemBody;
        User u;
        Message m;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = this.itemView.findViewById(R.id.tv_message);
            tvName = this.itemView.findViewById(R.id.tv_user_name);
            bmIcon = this.itemView.findViewById(R.id.user_icon);
            tvDate = this.itemView.findViewById(R.id.tv_date);
            itemBody = itemView.findViewById(R.id.message_body);
        }

        public void bind(int listIndex){
            m = messages.get(listIndex);
            if (m.userEmail != null) {
                Log.e("adaptrer n", m.message);
                User.getUserById(m.userEmail, new OnGetUser() {
                    @Override
                    public void onGet(User user) {
                        Log.e(";pouioykiyhiyhoi", "ligkghkj");
                        u = user;
                        tvName.setText(u.name);
                        tvMessage.setText(m.message);
                    /*     if (u != null){
                        tvName.setText(u.name + " " + u.family);
                        u.getIconAsync(new OnGetIcon() {
                            @Override
                            public void onLoad(Bitmap bitmap) {
                                bmIcon.setImageBitmap(bitmap);
                            }
                        });
                    }

                */
                    }
                });
            }
            else {
                Log.e("adapter", m.message);
            }
          /*  u = getUserByEmail(m.userEmail);

            tvMessage.setText(m.message);
            if (u != null){
                tvName.setText(u.name + " " + u.family);
                u.getIconAsync(new OnGetIcon() {
                    @Override
                    public void onLoad(Bitmap bitmap) {
                        bmIcon.setImageBitmap(bitmap);
                    }
                });
            }*/
        }





    }
}

