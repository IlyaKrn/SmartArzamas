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
import com.example.smartarzamas.firebaseobjects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DialogAdminAccountReject extends Dialog{

    private Button reject, cancel;
    private User user;

    public DialogAdminAccountReject(AppCompatActivity activity, User user) {
        super(activity);
        this.user = user;
    }
    public DialogAdminAccountReject(Fragment fragment, User user) {
        super(fragment);
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_admin_account_reject, container, false);
        reject = rootView.findViewById(R.id.bt_reject);
        cancel = rootView.findViewById(R.id.bt_cancel);

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freeze();
                User.getDatabase().child(user.id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.child("isModerator").getRef().setValue(false);
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
