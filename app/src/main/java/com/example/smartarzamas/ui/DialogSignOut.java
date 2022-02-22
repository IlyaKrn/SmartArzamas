package com.example.smartarzamas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.AuthActivity;
import com.example.smartarzamas.R;
import com.example.smartarzamas.SQLiteDatabase.SQLiteDbManager;

public class DialogSignOut extends Dialog {

    private Button signOut, cancel;

    public DialogSignOut(AppCompatActivity activity) {
        super(activity);
    }
    public DialogSignOut(Fragment fragment) {
        super(fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_sign_out, container, false);
        signOut = rootView.findViewById(R.id.bt_sign_out);
        cancel = rootView.findViewById(R.id.bt_cancel);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freeze();
                SQLiteDbManager manager = new SQLiteDbManager(context);
                manager.clear();
                Intent intent = new Intent(getActivity(), AuthActivity.class);
                startActivity(intent);
                destroy();
                getActivity().finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroy();
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
