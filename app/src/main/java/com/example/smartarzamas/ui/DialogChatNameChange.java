package com.example.smartarzamas.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Chat;
import com.example.smartarzamas.firebaseobjects.Message;
import com.example.smartarzamas.firebaseobjects.OnSetDataListener;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;

public class DialogChatNameChange extends Dialog{

    private Button change, cancel;
    private EditText etName;
    private TextView tvNameErr;
    Chat chat;
    User user;

    public DialogChatNameChange(AppCompatActivity activity, User user, Chat chat) {
        super(activity);
        this.user = user;
        this.chat = chat;
    }

    public DialogChatNameChange(Fragment fragment, User user, Chat chat) {
        super(fragment);
        this.user = user;
        this.chat = chat;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_chat_name_change, container, false);
        change = rootView.findViewById(R.id.bt_change_chat_name);
        cancel = rootView.findViewById(R.id.bt_cancel);
        etName = rootView.findViewById(R.id.et_chat_name);
        tvNameErr = rootView.findViewById(R.id.tv_name_err);

        // установка текуцщих значений
        etName.setText(chat.name);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroy();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // получение введенных данных
                final String name = etName.getText().toString();
                final String preName = chat.name;
                // скрытие предупреждений о некорректных данных
                Utils.hideWarning(tvNameErr);
                // если поля ввода не пустые
                if (name.length() > 0){
                    freeze();
                    Chat buffChat = chat;
                    buffChat.name = name;
                    String message = user.name + " " + user.family + " " + getString(R.string.user_change_chat_neme) + " \"" + preName + "\" " + getString(R.string.on) + " \"" + chat.name + "\"";
                    buffChat.messages.add(new Message(message, null, Chat.getDatabase().push().getKey(), null));

                    chat.setNewData(getContext(), buffChat, new OnSetDataListener<Chat>() {
                        @Override
                        public void onSetData(Chat data) {
                            destroy();
                        }

                        @Override
                        public void onNoConnection() {

                        }

                        @Override
                        public void onCanceled() {

                        }
                    });
                }
                // вывод предупреждения о пустых полях ввода
                else {
                    Utils.showWarning(tvNameErr, R.string.enter_new_name);
                }
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
