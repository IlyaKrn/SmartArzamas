package com.example.smartarzamas.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class Dialog extends Fragment {

    public static final String LOG_TAG = "FragmentTag";

    protected View rootView;
    protected FragmentManager fragmentManager;
    protected Context context;

    public Dialog(AppCompatActivity activity) {
        context = activity.getApplicationContext();
        fragmentManager = activity.getSupportFragmentManager();
    }

    public Dialog(Fragment fragment) {
        context = fragment.getActivity().getApplicationContext();
        fragmentManager = fragment.getChildFragmentManager();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return rootView;

    }
    public void create(int containerId){
        fragmentManager.beginTransaction().add(containerId, this).commit();
    }
    protected void destroy(){
        fragmentManager.beginTransaction().remove(this).commit();
    }
}
