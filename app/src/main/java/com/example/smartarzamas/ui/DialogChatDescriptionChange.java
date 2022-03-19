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

public class DialogChatDescriptionChange extends Dialog{

    private Button change, cancel;
    private EditText etDescription;
    private TextView tvDescriptionErr;
    Chat chat;
    User user;

    public DialogChatDescriptionChange(AppCompatActivity activity, User user, Chat chat) {
        super(activity);
        this.user = user;
        this.chat = chat;
    }

    public DialogChatDescriptionChange(Fragment fragment, User user, Chat chat) {
        super(fragment);
        this.user = user;
        this.chat = chat;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_chat_description_change, container, false);
        change = rootView.findViewById(R.id.bt_change_chat_description);
        cancel = rootView.findViewById(R.id.bt_cancel);
        etDescription = rootView.findViewById(R.id.et_chat_description);
        tvDescriptionErr = rootView.findViewById(R.id.tv_description_err);

        // установка текуцщих значений
        etDescription.setText(chat.description);

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
                final String description = etDescription.getText().toString();
                final String preDescription = chat.description;
                // скрытие предупреждений о некорректных данных
                Utils.hideWarning(tvDescriptionErr);
                // если поля ввода не пустые
                if (description.length() > 0){
                    freeze();
                    Chat buffChat = chat;
                    buffChat.description = description;
                    String message = user.name + " " + user.family + " " + getString(R.string.user_change_description_neme) + " \"" + preDescription + "\" " + getString(R.string.on) + " \"" + chat.description + "\"";
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
                    Utils.showWarning(tvDescriptionErr, R.string.enter_new_name);
                }
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
