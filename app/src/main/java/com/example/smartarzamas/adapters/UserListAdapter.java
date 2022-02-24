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
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetIcons;
import com.example.smartarzamas.firebaseobjects.OnGetUser;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.IconView;
import com.example.smartarzamas.support.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {

    private final OnStateClickListener onClickListener;
    private final ArrayList<User> users;
    private final Context context;
    private final User user;
    private Map<String, Bitmap> savedIcons = new HashMap<>();
    private Map<String, ArrayList<Bitmap>> savedImages = new HashMap<>();


    public UserListAdapter(Context context, ArrayList<User> users,/* ArrayList<User>  userList,*/ User user, OnStateClickListener onClickListener) {
        this.context = context;
        this.users = users;
        this.onClickListener = onClickListener;
        this.user = user;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_user_list, parent, false);

        UserHolder holder = new UserHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, @SuppressLint("RecyclerView") int position) {
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
        return users.size();
    }

    public interface OnStateClickListener{
        void onStateClick(int messagePosition);
    }

    class UserHolder extends RecyclerView.ViewHolder {

        ImageButton btMenu;
        ProgressBar progressImage;
        TextView tvName;
        IconView ivIcon;
        TextView tvDate;
        View itemBody;

        User u;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            btMenu = itemView.findViewById(R.id.bt_item_menu);

            tvName = this.itemView.findViewById(R.id.tv_user_name);
            ivIcon = this.itemView.findViewById(R.id.user_icon);
            itemBody = this.itemView.findViewById(R.id.item_body);
            progressImage = this.itemView.findViewById(R.id.progress);

        }

        public void bind(int listIndex) {
            u = users.get(listIndex);

            btMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            if (user.isModerator) {
                btMenu.setVisibility(View.VISIBLE);
            } else {
                btMenu.setVisibility(View.GONE);
            }
            User.getUserById(u.id, new OnGetUser() {
                @Override
                public void onGet(User user) {
                    u = user;
                    if (user.id.equals(u.id))
                        tvName.setText(user.name);
                    if (savedIcons.get(user.id) != null) {
                        if (user.id.equals(u.id)) {
                            ivIcon.setImageBitmap(savedIcons.get(user.id));
                            ivIcon.setVisibility(View.VISIBLE);
                            progressImage.setVisibility(View.GONE);
                        }
                    } else {
                        user.getIconAsync(context, new OnGetIcon() {
                            @Override
                            public void onLoad(Bitmap bitmap) {
                                savedIcons.put(user.id, bitmap);
                                if (user.id.equals(u.id)) {
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
