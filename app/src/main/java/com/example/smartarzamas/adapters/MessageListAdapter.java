package com.example.smartarzamas.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.smartarzamas.support.TableMessageImages;
import com.example.smartarzamas.support.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageHolder> {

    private final OnStateClickListener onClickListener;
    private final ArrayList<Message> messages;
    private final Context context;
    private final User user;
    private Map<String, Bitmap> savedIcons = new HashMap<>();
    private Map<String, ArrayList<Bitmap>> savedImages = new HashMap<>();


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

        TableMessageImages notMy_tlImages;
        ProgressBar notMy_progressImage;
        TextView notMy_tvMessage;
        TextView notMy_tvName;
        IconView notMy_ivIcon;
        TextView notMy_tvDate;
        View notMy_itemBody;

        TableMessageImages my_tlImages;
        View my_itemBody;
        TextView my_tvMessage;
        TextView my_tvName;
        TextView my_tvDate;

        View system_itemBody;
        TextView system_tvMessage;
        TextView system_tvDate;
        TableMessageImages system_tlImages;

        User u;
        Message m;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            notMy_tvMessage = this.itemView.findViewById(R.id.not_my_tv_message);
            notMy_tvName = this.itemView.findViewById(R.id.not_my_tv_user_name);
            notMy_ivIcon = this.itemView.findViewById(R.id.not_my_user_icon);
            notMy_tvDate = this.itemView.findViewById(R.id.not_my_tv_date);
            notMy_itemBody = this.itemView.findViewById(R.id.not_my_item_body);
            notMy_progressImage = this.itemView.findViewById(R.id.not_my_progress);
            notMy_tlImages = itemView.findViewById(R.id.not_my_lv_images);

            my_itemBody = this.itemView.findViewById(R.id.my_item_body);
            my_tvMessage = this.itemView.findViewById(R.id.my_tv_message);
            my_tvName = this.itemView.findViewById(R.id.my_tv_user_name);
            my_tvDate = this.itemView.findViewById(R.id.my_tv_date);
            my_tlImages = itemView.findViewById(R.id.my_lv_images);

            system_itemBody = this.itemView.findViewById(R.id.system_item_body);
            system_tvMessage = this.itemView.findViewById(R.id.system_tv_message);
            system_tvDate = this.itemView.findViewById(R.id.system_tv_date);
            system_tlImages = itemView.findViewById(R.id.system_lv_images);
        }

        public void bind(int listIndex){
            m = messages.get(listIndex);

            system_tlImages.removeBitmaps();
            notMy_tlImages.removeBitmaps();
            my_tlImages.removeBitmaps();

            if (m.imageRefs != null){
                if (savedImages.get(m.id) != null){
                    ArrayList<Bitmap> bitmaps = savedImages.get(m.id);
                    if (m.userId != null){
                        if (m.userId.equals(user.id)){
                            my_tlImages.setBitmaps(bitmaps);
                        }
                        else {
                            notMy_tlImages.setBitmaps(bitmaps);
                        }
                    }
                    else {
                        system_tlImages.setBitmaps(bitmaps);
                    }
                }
                else {
                    m.getIconsAsync(context, new OnGetIcons() {
                        @Override
                        public void onGet(ArrayList<Bitmap> bitmaps, Message message) {
                            savedImages.put(message.id, bitmaps);
                            if (m.equals(message)) {
                                if (m.userId != null){
                                    if (m.userId.equals(user.id)){
                                        my_tlImages.setBitmaps(bitmaps);
                                    }
                                    else {
                                        notMy_tlImages.setBitmaps(bitmaps);
                                    }
                                }
                                else {
                                    system_tlImages.setBitmaps(bitmaps);
                                }
                            }
                        }
                    });

                }
            }
            else {
                system_tlImages.removeBitmaps();
                notMy_tlImages.removeBitmaps();
                my_tlImages.removeBitmaps();
            }



            if (m.userId != null) {
                if (m.userId.equals(user.id)) {
                    showMyMessage();
                    my_tvMessage.setText(m.message);
                    my_tvName.setText(R.string.my_message_name);
                    my_tvDate.setText(Utils.getDateString());

                }
                else {
                    showNotMyMessage();
                    notMy_ivIcon.setVisibility(View.GONE);
                    notMy_progressImage.setVisibility(View.VISIBLE);
                    notMy_tvMessage.setText(m.message);
                    User.getUserById(m.userId, new OnGetUser() {
                        @Override
                        public void onGet(User user) {
                            u = user;
                            if (user.id.equals(m.userId))
                                notMy_tvName.setText(user.name);
                            if (savedIcons.get(user.id) != null){
                                if (user.id.equals(m.userId)) {
                                    notMy_ivIcon.setImageBitmap(savedIcons.get(user.id));
                                    notMy_ivIcon.setVisibility(View.VISIBLE);
                                    notMy_progressImage.setVisibility(View.GONE);
                                }
                            }
                            else {
                                user.getIconAsync(context, new OnGetIcon() {
                                    @Override
                                    public void onLoad(Bitmap bitmap) {
                                        savedIcons.put(user.id, bitmap);
                                        if (user.id.equals(m.userId)) {
                                            notMy_ivIcon.setImageBitmap(bitmap);
                                            notMy_ivIcon.setVisibility(View.VISIBLE);
                                            notMy_progressImage.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }

                        }
                    });
                }

            }
            else {
                showSystemMessage();
                system_tvMessage.setText(m.message);
                system_tvDate.setText(Utils.getDateString());
            }

        }
        private void showNotMyMessage(){
            notMy_itemBody.setVisibility(View.VISIBLE);
            my_itemBody.setVisibility(View.GONE);
            system_itemBody.setVisibility(View.GONE);
        }
        private void showMyMessage(){
            notMy_itemBody.setVisibility(View.GONE);
            my_itemBody.setVisibility(View.VISIBLE);
            system_itemBody.setVisibility(View.GONE);
        }
        private void showSystemMessage(){
            notMy_itemBody.setVisibility(View.GONE);
            my_itemBody.setVisibility(View.GONE);
            system_itemBody.setVisibility(View.VISIBLE);
        }

    }
}
