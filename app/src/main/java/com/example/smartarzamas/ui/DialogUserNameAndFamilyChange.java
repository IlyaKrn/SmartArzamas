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
import com.example.smartarzamas.firebaseobjects.OnUpdateUser;
import com.example.smartarzamas.support.SomethingMethods;
import com.example.smartarzamas.firebaseobjects.User;

public class DialogUserNameAndFamilyChange extends Dialog{

    private Button change, cancel;
    private EditText etName, etFamily;
    private TextView tvNameErr, tvFamilyErr;
    User user;

    public DialogUserNameAndFamilyChange(AppCompatActivity activity, User user) {
        super(activity);
        this.user = user;
    }
    public DialogUserNameAndFamilyChange(Fragment fragment, User user) {
        super(fragment);
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_user_name_and_family_change, container, false);
        change = rootView.findViewById(R.id.bt_change_user_name_and_family);
        cancel = rootView.findViewById(R.id.bt_cancel);
        tvNameErr = rootView.findViewById(R.id.tv_name_err);
        tvFamilyErr = rootView.findViewById(R.id.tv_family_err);
        etName = rootView.findViewById(R.id.et_user_name);
        etFamily = rootView.findViewById(R.id.et_user_family);

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
                final String family = etFamily.getText().toString();
                // скрытие предупреждений о некорректных данных
                SomethingMethods.hideWarning(tvNameErr);
                SomethingMethods.hideWarning(tvFamilyErr);
                // установка текуцщих значений
                etName.setText(user.name);
                etFamily.setText(user.family);
                // если поля ввода не пустые
                if (name.length() > 0 && family.length() > 0){
                    User buffUser = user;
                    buffUser.name = name;
                    buffUser.family = family;
                    user.setNewData(buffUser, new OnUpdateUser() {
                        @Override
                        public void onUpdate(User user) {
                            destroy();

                        }
                    });
                }
                // вывод предупреждения о пустых полях ввода
                else {
                    if (name.length() == 0){
                        SomethingMethods.showWarning(tvNameErr, R.string.enter_new_name);
                    }
                    if (family.length() == 0){
                        SomethingMethods.showWarning(tvFamilyErr, R.string.enter_new_family);
                    }

                }
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }




}
