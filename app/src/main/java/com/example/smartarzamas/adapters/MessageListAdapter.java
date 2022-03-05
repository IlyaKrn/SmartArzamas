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
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetIcons;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.IconView;
import com.example.smartarzamas.support.TableMessageImages;
import com.example.smartarzamas.support.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageListAdapter extends FirebaseAdapter<Message, MessageListAdapter.MessageHolder> {

    private Map<String, Bitmap> savedIcons = new HashMap<>();
    private Map<String, ArrayList<Bitmap>> savedImages = new HashMap<>();

    public MessageListAdapter(Context context, User user, boolean isAdmin, ArrayList<Message> items, OnStateClickListener<Message> onItemClickListener) {
        super(context, user, isAdmin, items, onItemClickListener);
    }

    @Override
    protected MessageHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_list, parent, false));
    }

    public class MessageHolder extends FirebaseHolder<Message>{

        private final ImageButton btMenu;
        private final TableMessageImages notMy_tlImages;
        private final ProgressBar notMy_progressImage;
        private final TextView notMy_tvMessage;
        private final TextView notMy_tvName;
        private final IconView notMy_ivIcon;
        private final TextView notMy_tvDate;
        private final View notMy_itemBody;

        private final TableMessageImages my_tlImages;
        private final View my_itemBody;
        private final TextView my_tvMessage;
        private final TextView my_tvName;
        private final TextView my_tvDate;

        private final View system_itemBody;
        private final TextView system_tvMessage;
        private final TextView system_tvDate;
        private final TableMessageImages system_tlImages;

        User u;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            btMenu = itemView.findViewById(R.id.bt_item_menu);

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

        @Override
        public void bind(int position) {
            item = getItem(position);
            btMenu.setVisibility(View.GONE);

            system_tlImages.removeBitmaps();
            notMy_tlImages.removeBitmaps();
            my_tlImages.removeBitmaps();
            if (item.imageRefs != null){
                if (savedImages.get(item.id) != null){
                    ArrayList<Bitmap> bitmaps = savedImages.get(item.id);
                    if (item.userId != null){
                        if (item.userId.equals(user.id)){
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
                    item.getIconsAsync(context, new OnGetIcons() {
                        @Override
                        public void onGet(ArrayList<Bitmap> bitmaps, Message message) {
                            savedImages.put(message.id, bitmaps);
                            if (item.equals(message)) {
                                if (item.userId != null){
                                    if (item.userId.equals(user.id)){
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



            if (item.userId != null) {
                if (item.userId.equals(user.id)) {
                    showMyMessage();
                    my_tvMessage.setText(item.message);
                    my_tvName.setText(R.string.my_message_name);
                    my_tvDate.setText(Utils.getDateString());

                }
                else {
                    showNotMyMessage();
                    notMy_ivIcon.setVisibility(View.GONE);
                    notMy_progressImage.setVisibility(View.VISIBLE);
                    notMy_tvMessage.setText(item.message);
                    User.getUserById(item.userId, new OnGetDataListener<User>() {
                        @Override
                        public void onGetData(User data) {
                            u = data;
                            if (user.id.equals(item.userId))
                                notMy_tvName.setText(user.name);
                            if (savedIcons.get(user.id) != null){
                                if (user.id.equals(item.userId)) {
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
                                        if (user.id.equals(item.userId)) {
                                            notMy_ivIcon.setImageBitmap(bitmap);
                                            notMy_ivIcon.setVisibility(View.VISIBLE);
                                            notMy_progressImage.setVisibility(View.GONE);
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
            else {
                showSystemMessage();
                system_tvMessage.setText(item.message);
                system_tvDate.setText(Utils.getDateString());
            }
        }

        @Override
        public void bindAdmin(int position) {
            btMenu.setVisibility(View.VISIBLE);
            btMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
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
