package com.example.smartarzamas.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.User;

public class DialogConfirm extends Dialog {

    private Button confirm, cancel;
    private OnConfirmListener onConfirmListener;
    private TextView tvTitle, tvData;
    String title;
    String confirmText;
    String data;

    public DialogConfirm(AppCompatActivity activity, String title, String confirm, String data, OnConfirmListener onConfirmListener) {
        super(activity);
        this.onConfirmListener = onConfirmListener;
        this.title = title;
        this.confirmText = confirm;
        this.data = data;
    }
    public DialogConfirm(Fragment fragment, String title, String confirm, String data, OnConfirmListener onConfirmListener) {
        super(fragment);
        this.onConfirmListener = onConfirmListener;
        this.title = title;
        this.confirmText = confirm;
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_confirm, container, false);
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvData = rootView.findViewById(R.id.tv_data);
        confirm = rootView.findViewById(R.id.bt_confirm);
        cancel = rootView.findViewById(R.id.bt_cancel);

        confirm.setText(confirmText);
        tvTitle.setText(title);
        tvData.setText(data);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmListener.onConfirm(DialogConfirm.this);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmListener.onCancel(DialogConfirm.this);
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
