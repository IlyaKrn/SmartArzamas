package com.example.smartarzamas;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.adapters.FirebaseAdapter;
import com.example.smartarzamas.adapters.UserListAdapter;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetListDataListener;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.DialogChatDescriptionChange;
import com.example.smartarzamas.ui.DialogChatIconChange;
import com.example.smartarzamas.ui.DialogChatNameChange;
import com.example.smartarzamas.ui.OnIconChangeListener;

import java.util.ArrayList;

public class ChatSettingsActivity extends FirebaseActivity {

    private ImageButton btClose;
     private ImageView chatIcon;
    private TextView tvChatName;
    private TextView tvChatDescription;
    private ProgressBar progressBar;
    private RecyclerView rvMembers;
    private ArrayList<User> members = new ArrayList<User>();
    private UserListAdapter adapter;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        init();
        updateViewData();
        chat.addChatListener(this, "12", new OnGetDataListener<Chat>() {
            @Override
            public void onGetData(Chat data) {
                updateViewData();
            }

            @Override
            public void onVoidData() {
                Toast.makeText(ChatSettingsActivity.this, getString(R.string.data_not_find), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {
                Toast.makeText(ChatSettingsActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        User.addUserListListener(this, "13", new OnGetListDataListener<User>() {
            @Override
            public void onGetData(ArrayList<User> data) {
                if (members.size() > 0) members.clear();
                for (User u : data) {
                    if (chat.isMember(u))
                        members.add(u);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onVoidData() {
                if (members.size() > 0) members.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {
                Toast.makeText(ChatSettingsActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    // инициализация
    void init(){
        chat = (Chat) getIntent().getSerializableExtra(CHAT_ID);
        chatIcon = findViewById(R.id.chat_icon);
        tvChatName = findViewById(R.id.chat_name);
        tvChatDescription = findViewById(R.id.chat_description);
        progressBar = findViewById(R.id.progress);
        rvMembers = findViewById(R.id.rv_members);
        btClose = findViewById(R.id.bt_close);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserListAdapter(this, user, false, members, new FirebaseAdapter.OnStateClickListener<User>() {
            @Override
            public void onClick(User item) {

            }

            @Override
            public void onLongClick(User item) {

            }
        });
        rvMembers.setAdapter(adapter);
    }
    private void updateViewData(){
        if (chatIcon.getDrawable() == null){
            chatIcon.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        Chat.getChatById(this, chat.id, new OnGetDataListener<Chat>() {
            @Override
            public void onGetData(Chat data) {
                ChatSettingsActivity.this.chat = data;
                tvChatName.setText(chat.name);
                tvChatDescription.setText(chat.description);
                chat.getIconAsync(ChatSettingsActivity.this.getApplicationContext(), new OnGetIcon() {
                    public void onLoad(Bitmap bitmap) {
                        chatIcon.setImageBitmap(bitmap);
                        chatIcon.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onVoidData() {
                Toast.makeText(ChatSettingsActivity.this, getString(R.string.data_not_find), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {
                Toast.makeText(ChatSettingsActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        chat.removeObjectDataListener("12");
        User.removeDataListener("13");
        super.onDestroy();
    }
}