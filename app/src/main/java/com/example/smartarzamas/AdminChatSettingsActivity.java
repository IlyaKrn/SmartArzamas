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
import com.example.smartarzamas.firebaseobjects.OnDeleteDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.OnGetListDataListener;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.ui.DialogChatDescriptionChange;
import com.example.smartarzamas.ui.DialogChatIconChange;
import com.example.smartarzamas.ui.DialogChatNameChange;
import com.example.smartarzamas.ui.DialogConfirm;
import com.example.smartarzamas.ui.OnConfirmListener;
import com.example.smartarzamas.ui.OnIconChangeListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AdminChatSettingsActivity extends FirebaseActivity {

    private ImageButton btClose;
    private Button btChangeName;
    private Button btChangeIcon;
    private Button btDeleteChat;
    private Button btChangedDescription;
    private ImageView chatIcon;
    private TextView tvChatName;
    private TextView tvChatDescription;
    private ProgressBar progressBar;
    private RecyclerView rvMembers;
    private ArrayList<User> members = new ArrayList<>();
    private UserListAdapter adapter;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat_settings);
        init();
        updateViewData();
        chat.addChatListener("9", new OnGetDataListener<Chat>() {
            @Override
            public void onGetData(Chat data) {
                updateViewData();
            }

            @Override
            public void onVoidData() {
                Toast.makeText(AdminChatSettingsActivity.this, getString(R.string.data_not_find), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {
                Toast.makeText(AdminChatSettingsActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        User.addUserListListener("10", new OnGetListDataListener<User>() {
            @Override
            public void onGetData(ArrayList<User> data) {
                if (members.size() > 0)members.clear();
                for (User u : data){
                    if(chat.isMember(u))
                        members.add(u);
                }
                adapter.notifyDataSetChanged();
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
        btChangedDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChatDescriptionChange dialog = new DialogChatDescriptionChange(AdminChatSettingsActivity.this, user, chat);
                dialog.create(R.id.fragmentContainerView);
            }
        });
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btChangeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogChatIconChange dialog = new DialogChatIconChange(AdminChatSettingsActivity.this, user, chat);
                dialog.create(R.id.fragmentContainerView);
                dialog.setOnIconChangeListener(new OnIconChangeListener() {
                    @Override
                    public void onChange(Bitmap bitmap) {
                        chatIcon.setImageBitmap(bitmap);
                    }
                });
            }
        });
        btChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                    @Override
                    public void isConnected() {
                        DialogChatNameChange dialog = new DialogChatNameChange(AdminChatSettingsActivity.this, user, chat);
                        dialog.create(R.id.fragmentContainerView);
                    }
                });
            }
        });

        btDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogConfirm dialog = new DialogConfirm(AdminChatSettingsActivity.this, getString(R.string.delete_chat), getString(R.string.delete), getString(R.string.realy_delete_chat), new OnConfirmListener() {
                    @Override
                    public void onConfirm(DialogConfirm d) {
                        d.freeze();
                        chat.removeFromDatabase(new OnDeleteDataListener() {
                            @Override
                            public void onDataDelete(DatabaseReference deleteRef){
                                chat.removeFromDatabase(new OnDeleteDataListener() {
                                    @Override
                                    public void onDataDelete(DatabaseReference deleteRef) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.delete_account_succesful), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onNoConnection() {

                                    }

                                    @Override
                                    public void onCanceled() {
                                        Toast.makeText(AdminChatSettingsActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                                    }
                                });
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
                    public void onCancel(DialogConfirm d) {
                        d.destroy();
                    }
                });
                dialog.create(R.id.fragmentContainerView);
            }
        });
    }
    // инициализация
    void init(){
        chat = (Chat) getIntent().getSerializableExtra(CHAT_ID);
        chatIcon = findViewById(R.id.chat_icon);
        tvChatName = findViewById(R.id.chat_name);
        btDeleteChat = findViewById(R.id.bt_delete_chat);
        tvChatDescription = findViewById(R.id.chat_description);
        progressBar = findViewById(R.id.progress);
        rvMembers = findViewById(R.id.rv_members);
        btChangeIcon = findViewById(R.id.bt_change_chat_icon);
        btChangeName = findViewById(R.id.bt_change_chat_name);
        btChangedDescription = findViewById(R.id.bt_change_chat_description);
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
        Chat.getChatById(chat.id, new OnGetDataListener<Chat>() {

            @Override
            public void onGetData(Chat data) {
                AdminChatSettingsActivity.this.chat = data;
                tvChatName.setText(chat.name);
                tvChatDescription.setText(chat.description);
                chat.getIconAsync(AdminChatSettingsActivity.this.getApplicationContext(), new OnGetIcon() {
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
                Toast.makeText(AdminChatSettingsActivity.this, getString(R.string.data_not_find), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoConnection() {

            }

            @Override
            public void onCanceled() {
                Toast.makeText(AdminChatSettingsActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        chat.removeObjectDataListener("9");
        User.removeDataListener("10");
        super.onDestroy();
    }
}