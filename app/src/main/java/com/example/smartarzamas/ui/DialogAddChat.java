package com.example.smartarzamas.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.R;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.support.Category;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class DialogAddChat extends Dialog {

    private Button addChat, cancel;
    private EditText etName, etDescription;
    private TextView tvCategory, tvNameErr, tvCategoryErr, tvDescriptionErr;
    private User user;

    public DialogAddChat(AppCompatActivity activity, User user) {
        super(activity);
        this.user = user;
    }
    public DialogAddChat(Fragment fragment, User user) {
        super(fragment);
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_add_chat, container, false);
        addChat = rootView.findViewById(R.id.bt_create);
        cancel = rootView.findViewById(R.id.bt_cancel);
        etName = rootView.findViewById(R.id.et_chat_name);
        etDescription = rootView.findViewById(R.id.et_chat_description);
        tvNameErr = rootView.findViewById(R.id.tv_chat_name_err);
        tvCategoryErr = rootView.findViewById(R.id.tv_category_err);
        tvDescriptionErr = rootView.findViewById(R.id.tv_description_err);
        tvNameErr = rootView.findViewById(R.id.tv_chat_name_err);
        tvCategory = rootView.findViewById(R.id.tv_chat_category);

        addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // получение введенных данных
                String name = etName.getText().toString();
                String category = tvCategory.getText().toString();
                String description = etDescription.getText().toString();
                // скрытие предупреждений о некорректных данных
                Utils.hideWarning(tvNameErr, tvCategoryErr, tvDescriptionErr);

                // если поля ввода не пустые
                if (name.length() > 0 && category.length() > 0) {
                    freeze();
                    Chat chat;
                    ArrayList<Message> messages = new ArrayList<>();
                    messages.add(new Message(getResources().getString(R.string.default_first_message), null, Chat.getDatabase().push().getKey(), null));
                    String chatId = Chat.getDatabase().push().getKey();
                    if (description.length() > 0)
                        chat = new Chat(name, description, chatId, messages, category, user);
                    else
                        chat = new Chat(name, getResources().getString(R.string.default_chat_description), chatId, messages, category, user);


                    Utils.isConnected(context, new Utils.Connection() {
                        @Override
                        public void isConnected() {
                            Chat.getDatabase().child(chatId).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    destroy();
                                }
                            });
                        }
                    });
                }
                // вывод предупреждения о пустых полях ввода
                else {
                    if (name.length() == 0){
                        Utils.showWarning(tvNameErr, R.string.enter_chat_name);
                    }
                    if (category.length() == 0){
                        Utils.showWarning(tvCategoryErr, R.string.enter_category);
                    }

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroy();
            }
        });
        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.inflate(R.menu.popup_menu_map);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            // ямы на дорогах
                            case R.id.pits_on_roads:
                                tvCategory.setText(Category.PITS_ON_ROADS);
                                break;
                            // лужи
                            case R.id.puddles:
                                tvCategory.setText(Category.PUDDLES);
                                break;
                            // достопримечатльности
                            case R.id.sights:
                                tvCategory.setText(Category.SIGHTS);
                                break;
                            // снег
                            case R.id.snow:
                                tvCategory.setText(Category.SNOW);
                                break;
                            // другое
                            case R.id.other:
                                tvCategory.setText(Category.OTHER);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

       return super.onCreateView(inflater, container, savedInstanceState);
    }
}
