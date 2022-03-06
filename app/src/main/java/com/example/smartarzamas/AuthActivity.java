package com.example.smartarzamas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.User;
import com.example.smartarzamas.support.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.io.Serializable;

public class AuthActivity extends FirebaseActivity {

    LinearLayout layoutSignIn; // разметка для входа
    LinearLayout layoutSignUp; // разметка для регистрации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();
        signIn();

    }
    // инициализация полей
    private void init(){
        layoutSignIn = (LinearLayout) findViewById(R.id.layout_sign_in);
        layoutSignUp = (LinearLayout) findViewById(R.id.layout_sign_up);
    }

    // finish с проверкой user != null
    @Override
    public void finish() {
        if (user != null) {
            // передача пользователя и запуск следующей активности
            if (user.isModerator) {
                Intent intent = new Intent(AuthActivity.this, AdminHubActivity.class);
                intent.putExtra("user", (Serializable) user);
                Toast.makeText(getApplicationContext(), getString(R.string.you_sign_in_as) + " " + user.name + " " + user.family, Toast.LENGTH_LONG).show();
                startActivity(intent);
                Log.d(LOG_TAG, "AdminHubActivity starting");
            }
            else {


                Intent intent = new Intent(AuthActivity.this, HubActivity.class);
                intent.putExtra("user", (Serializable) user);
                Toast.makeText(getApplicationContext(), getString(R.string.you_sign_in_as) + " " + user.name + " " + user.family, Toast.LENGTH_LONG).show();
                startActivity(intent);
                Log.d(LOG_TAG, "HubActivity starting");
            }
            super.finish();
        }
        else {
            Log.d(LOG_TAG, "finish canceled");
        }
    }

    void signIn(){
        Log.d(LOG_TAG, "called signIn layout");
        layoutSignIn.setVisibility(View.VISIBLE);
        layoutSignUp.setVisibility(View.GONE);
        EditText etUserEmail = (EditText)layoutSignIn.findViewById(R.id.et_email_in);
        EditText etUserPassword = (EditText)layoutSignIn.findViewById(R.id.et_password_in);
        CheckBox cbAlwaysUse = (CheckBox) layoutSignIn.findViewById(R.id.cb_always_use_in);
        Button btSignIn = findViewById(R.id.bt_sign_in_in);
        Button btSignUp = findViewById(R.id.bt_sign_up_in);
        Button btForgotPassword = findViewById(R.id.bt_forgot_password_in);
        // предупреждения о некорректно введенных данных
        TextView tvLoginErr = layoutSignIn.findViewById(R.id.tv_email_err_in);
        TextView tvPasswordErr = layoutSignIn.findViewById(R.id.tv_password_err_in);
        // проверка currentUser
        if (currentUser != null){
            Log.d(LOG_TAG, "current user from SQLite: email is " + currentUser.login + " password is " + currentUser.password);
            User.getUserById(currentUser.id, new OnGetDataListener<User>() {
                @Override
                public void onGetData(User data) {
                    AuthActivity.this.user = data;
                }

                @Override
                public void onVoidData() {

                }

                @Override
                public void onNoConnection() {

                }

                @Override
                public void onCanceled() {
                    Toast.makeText(AuthActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                }
            });
            etUserEmail.setText(getSQLiteCurrentUser().login);
            etUserPassword.setText(getSQLiteCurrentUser().password);
        }
        else {
            Log.d(LOG_TAG, "no current user from SQLite");
        }

        // обработка нажатия на кнопку "Войти" (в диалоге)
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick btSignIn in signIn layout called");
                // получение введенных данных
                final String login = etUserEmail.getText().toString();
                final String password = etUserPassword.getText().toString();
                // скрытие предупреждений о некорректных данных
                Utils.hideWarning(tvLoginErr, tvPasswordErr);
                // если поля ввода не пустые
                if (login.length() > 0 && password.length() > 0){
                    Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                        @Override
                        public void isConnected() {
                            auth.signInWithEmailAndPassword(login, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // если вход прошел успешно
                                    if (task.isSuccessful()){
                                        if (auth.getCurrentUser().isEmailVerified()) {
                                            User.getUserById(Utils.getKeyString(login), new OnGetDataListener<User>() {
                                                @Override
                                                public void onGetData(User data) {
                                                    if (!data.banned) {
                                                        AuthActivity.this.user = data;
                                                        if (cbAlwaysUse.isChecked()) {
                                                            manager.clear();
                                                            manager.insertToDb(login, password, user.id);
                                                            Log.d(LOG_TAG, "new current user was wrote in SQLite: " + currentUser.login + " password: " + currentUser.password);
                                                        } else if (currentUser != null) {
                                                            if (currentUser.login.equals(login)) {
                                                                manager.clear();
                                                                Log.d(LOG_TAG, "SQLite cleaned");
                                                            }
                                                        } else if (currentUser == null) {
                                                            manager.clear();
                                                            manager.insertToDb(login, password, user.id);
                                                            currentUser = manager.getCurrentUser();
                                                            manager.clear();
                                                            Log.d(LOG_TAG, "SQLite cleaned");
                                                        }
                                                        // передача пользователя и запуск следующей активности
                                                        finish();
                                                    }
                                                    else {
                                                        Toast.makeText(AuthActivity.this, getString(R.string.you_banned), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onVoidData() {
                                                    Toast.makeText(AuthActivity.this, getString(R.string.data_not_find), Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onNoConnection() {

                                                }

                                                @Override
                                                public void onCanceled() {
                                                    Toast.makeText(AuthActivity.this, getString(R.string.databese_request_canceled), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else {
                                            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext(), R.string.verification_email_was_sand, Toast.LENGTH_LONG).show();
                                                    Utils.showWarning(tvLoginErr, R.string.email_no_verificated);
                                                    Log.e(LOG_TAG, "email " + auth.getCurrentUser().getEmail() + " is not verified");
                                                }
                                            });
                                        }
                                    }
                                    else {
                                        // установка предупреждений
                                        Utils.showWarning(tvLoginErr, R.string.wrong_login);
                                        Utils.showWarning(tvPasswordErr, R.string.wrong_password);
                                        Log.e(LOG_TAG, "wrong login or password");
                                    }
                                }
                            });
                        }
                    });

                }
                // вывод предупреждения о пустых полях ввода
                else {
                    if (login.length() == 0){
                        Utils.showWarning(tvLoginErr, R.string.enter_login);
                        Log.e(LOG_TAG, "login cannot be a null");
                    }
                    if (password.length() == 0){
                        Utils.showWarning(tvPasswordErr, R.string.enter_password);
                        Log.e(LOG_TAG, "password cannot be a null");
                    }
                }
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick btSignUp in signIn layout was called");
                signUp();
            }
        });

        btForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick btForgotPassword was called");
                Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                    @Override
                    public void isConnected() {
                        if ((!etUserEmail.getText().toString().equals("")) && etUserEmail.getText() != null) {
                            auth.sendPasswordResetEmail(etUserEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), R.string.email_reset_password, Toast.LENGTH_LONG).show();
                                        Log.d(LOG_TAG, "email for reset password send on address " + auth.getCurrentUser().getEmail());
                                    }
                                }
                            });
                        }
                        else {
                            Utils.showWarning(tvLoginErr, R.string.enter_login);
                        }
                    }
                });

            }
        });


    }

    void signUp() {
        Log.d(LOG_TAG, "called signUp  layout");
        layoutSignUp.setVisibility(View.VISIBLE);
        layoutSignIn.setVisibility(View.GONE);
        EditText etUserName = (EditText)layoutSignUp.findViewById(R.id.et_user_name_up);
        EditText etUserFamily = (EditText)layoutSignUp.findViewById(R.id.et_user_family_up);
        EditText etUserEmail = (EditText)layoutSignUp.findViewById(R.id.et_email_up);
        EditText etUserPassword = (EditText)layoutSignUp.findViewById(R.id.et_password_up);
        EditText etUserSecondPassword = (EditText)layoutSignUp.findViewById(R.id.et_second_password_up);
        CheckBox cbAlwaysUse = (CheckBox) layoutSignUp.findViewById(R.id.cb_always_use_up);
        Button btSignUp = layoutSignUp.findViewById(R.id.bt_sign_up_up);
        Button btSignIn = layoutSignUp.findViewById(R.id.bt_sign_in_up);
        Button btSignInFinally = layoutSignUp.findViewById(R.id.bt_sign_in_finaly_up);
        Button btResendEmail = layoutSignUp.findViewById(R.id.bt_resend_varification_email_up);
        TextView tvLoginErr = layoutSignUp.findViewById(R.id.tv_email_err_up);
        TextView tvPasswordErr = layoutSignUp.findViewById(R.id.tv_password_err_up);
        TextView tvSecondPasswordErr = layoutSignUp.findViewById(R.id.tv_second_password_err_up);
        TextView tvNameErr = layoutSignUp.findViewById(R.id.tv_name_err_up);
        TextView tvFamilyErr = layoutSignUp.findViewById(R.id.tv_family_err_up);

        etUserEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.d(LOG_TAG, "onEditorAction etUserEmail in signUp layout was called");
                btSignInFinally.setVisibility(View.GONE);
                btResendEmail.setVisibility(View.GONE);
                btSignUp.setVisibility(View.VISIBLE);
                return false;
            }
        });
        // вход в аккаунт
        btSignInFinally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick btSignInFinally in signUp layout called");
                // получение введенных данных
                final String name = etUserName.getText().toString();
                final String family = etUserFamily.getText().toString();
                final String login = etUserEmail.getText().toString();
                final String password = etUserPassword.getText().toString();
                final String password2 = etUserSecondPassword.getText().toString();
                // скрытие предупреждений о некорректных данных
                Utils.hideWarning(tvLoginErr, tvPasswordErr, tvSecondPasswordErr, tvNameErr, tvFamilyErr);

                // если поля ввода не пустые
                if (login.length() > 0 && password2.length() > 0 && password.length() > 0 && name.length() > 0 && family.length() > 0) {
                    // если первый и второлй пароль совпадают
                    if (password.equals(password2)) {
                        Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                            @Override
                            public void isConnected() {
                                auth.signInWithEmailAndPassword(login, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            if (auth.getCurrentUser().isEmailVerified()) {// запись пользователя в бд
                                                final String id = Utils.getKeyString(login);
                                                Log.i(LOG_TAG, "created new key with value " + id);
                                                /*
                                                dbUsers.child(id).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {



                                                 */
                                                        Log.i(LOG_TAG, "key with value " + id + " is unique in users database");
                                                        user = new User(login, name, family, null, id);
                                                        dbUsers.child(user.id).setValue(user);
                                                        Log.i(LOG_TAG, "user was created and written in database");
                                                        if (cbAlwaysUse.isChecked()) {
                                                            manager.clear();
                                                            manager.insertToDb(login, password, user.id);
                                                            Log.d(LOG_TAG, "new current user was wrote in SQLite: " + currentUser.login + " password: " + currentUser.password);
                                                        } else if (currentUser != null) {
                                                            if (currentUser.login.equals(login)) {
                                                                manager.clear();
                                                                Log.d(LOG_TAG, "SQLite cleaned");
                                                            }
                                                        } else if (currentUser == null) {
                                                            manager.clear();
                                                            manager.insertToDb(login, password, user.id);
                                                            currentUser = manager.getCurrentUser();
                                                            manager.clear();
                                                            Log.d(LOG_TAG, "SQLite cleaned");
                                                        }
                                                        // передача пользователя и запуск следующей активности
                                                      //  dbUsers.removeEventListener(this);
                                                        finish();
                                                    }
                                            /*
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.e(LOG_TAG, "checking key was canceled");
                                                        dbUsers.removeEventListener(this);
                                                    }
                                                });
                                            }

                                             */
                                            else{
                                                Toast.makeText(getApplicationContext(), R.string.email_no_verificated, Toast.LENGTH_LONG).show();
                                                Log.e(LOG_TAG, "email " + auth.getCurrentUser().getEmail() + " is not verified");

                                            }
                                        }
                                        else {
                                            if (auth.getCurrentUser().isEmailVerified()){
                                                Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_LONG).show();
                                                Log.e(LOG_TAG, "wrong password");
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
                                                Log.e(LOG_TAG, "error");
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                    // вывод предупреждения о различии пароля
                    else {
                        Utils.showWarning(tvSecondPasswordErr, R.string.wrong_password);
                        Log.e(LOG_TAG, "wrong password");
                    }
                }
                // вывод предупреждения о пустых полях ввода
                else {
                    if (login.length() == 0){
                        Utils.showWarning(tvLoginErr, R.string.enter_login);
                        Log.e(LOG_TAG, "login cannot be a null");
                    }
                    if (password.length() == 0){
                        Utils.showWarning(tvPasswordErr, R.string.enter_password);
                        Log.e(LOG_TAG, "password cannot be a null");
                    }
                    if (password2.length() == 0){
                        Utils.showWarning(tvSecondPasswordErr, R.string.enter_second_password);
                        Log.e(LOG_TAG, "second password cannot be a null");
                    }
                    if (name.length() == 0){
                        Utils.showWarning(tvNameErr, R.string.enter_name);
                        Log.e(LOG_TAG, "name cannot be a null");
                    }
                    if (family.length() == 0){
                        Utils.showWarning(tvFamilyErr, R.string.enter_family);
                        Log.e(LOG_TAG, "family cannot be a null");
                    }
                }


            }
        });
        // обработка нажатия на кнопку "Зарегистрироваться" (в диалоге)
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick btSignUp in signUp layout called");
                // получение введенных данных
                final String name = etUserName.getText().toString();
                final String family = etUserFamily.getText().toString();
                final String login = etUserEmail.getText().toString();
                final String password = etUserPassword.getText().toString();
                final String password2 = etUserSecondPassword.getText().toString();
                // скрытие предупреждений о некорректных данных
                Utils.hideWarning(tvLoginErr, tvPasswordErr, tvSecondPasswordErr, tvNameErr, tvFamilyErr);

                // если поля ввода не пустые
                if (login.length() > 0 && password2.length() > 0 && password.length() > 0 && name.length() > 0 && family.length() > 0) {
                    // если первый и второлй пароль совпадают
                    if (password.equals(password2)) {
                        if (password.length() > 5) {
                            Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                                @Override
                                public void isConnected() {
                                    auth.createUserWithEmailAndPassword(login, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            // еслм регистрация прошла успешно
                                            if (task.isSuccessful()) {
                                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(LOG_TAG, "email for verification email send on address " + auth.getCurrentUser().getEmail());
                                                            Toast.makeText(getApplicationContext(), R.string.verification_email_was_sand, Toast.LENGTH_LONG).show();
                                                            btSignUp.setVisibility(View.GONE);
                                                            btResendEmail.setVisibility(View.VISIBLE);
                                                            btSignInFinally.setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.e(LOG_TAG, "verification email send error");
                                                Toast.makeText(getApplicationContext(), R.string.error_of_email_send, Toast.LENGTH_LONG).show();
                                                btSignUp.setVisibility(View.GONE);
                                                btResendEmail.setVisibility(View.VISIBLE);
                                                btSignInFinally.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            Utils.showWarning(tvPasswordErr, R.string.short_password);
                            Utils.showWarning(tvSecondPasswordErr, R.string.short_password);
                        }
                    }
                    // вывод предупреждения о различии пароля
                    else {
                        Utils.showWarning(tvSecondPasswordErr, R.string.wrong_password);
                        Log.e(LOG_TAG, "password is different");
                    }
                }
                // вывод предупреждения о пустых полях ввода
                else {
                    if (login.length() == 0) {
                        Utils.showWarning(tvLoginErr, R.string.enter_login);
                        Log.e(LOG_TAG, "login cannot be a null");
                    }
                    if (password.length() == 0){
                        Utils.showWarning(tvPasswordErr, R.string.enter_password);
                        Log.e(LOG_TAG, "password cannot be a null");
                    }
                    if (password2.length() == 0){
                        Utils.showWarning(tvSecondPasswordErr, R.string.enter_second_password);
                        Log.e(LOG_TAG, "second password cannot be a null");
                    }
                    if (name.length() == 0){
                        Utils.showWarning(tvNameErr, R.string.enter_name);
                        Log.e(LOG_TAG, "name cannot be a null");
                    }
                    if (family.length() == 0){
                        Utils.showWarning(tvFamilyErr, R.string.enter_family);
                        Log.e(LOG_TAG, "family cannot be a null");
                    }
                }


            }
        });
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick btSignIn in signUp layout was called");
                signIn();
            }
        });
        // повтор верификации
        btResendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick btResendEmail in signUp layout called");
                Utils.isConnected(getApplicationContext(), new Utils.Connection() {
                    @Override
                    public void isConnected() {
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), R.string.verification_email_was_sand, Toast.LENGTH_LONG).show();
                                    btSignUp.setVisibility(View.GONE);
                                    Log.d(LOG_TAG, "email for verification email send on address " + auth.getCurrentUser().getEmail());
                                }
                            }
                        });
                    }
                });

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}