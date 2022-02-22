package com.example.smartarzamas.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public abstract class Dialog extends Fragment {

    public static final String LOG_TAG = "FragmentTag";

    protected View rootView;
    protected FragmentManager fragmentManager;
    protected Context context;

    private static ArrayList<Dialog> currentDialogs = new ArrayList<>();

    private OnDestroyListener onDestroyListener;

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
        for (Dialog d : currentDialogs) {
            d.destroy();
        }
        fragmentManager.beginTransaction().add(containerId, this).commit();
        currentDialogs.add(this);
    }
    public void destroy(){
        try {
            fragmentManager.beginTransaction().remove(this).commit();
            if (onDestroyListener != null) {
                onDestroyListener.onDestroy();
            }
        } catch (Exception e){
            Log.w(LOG_TAG, e.getMessage());
        }
    }

    public void setOnDestroyListener(OnDestroyListener onDestroyListener) {
        this.onDestroyListener = onDestroyListener;
    }
    protected void freeze(){
        setClickable((ViewGroup) rootView, false);
    }

    protected void defreeze(){
        setClickable((ViewGroup) rootView, true);
    }
    public void setClickable(View view, boolean clickable) {
        if (view != null) {
            view.setClickable(clickable);
            if (view instanceof ViewGroup) {
                ViewGroup vg = ((ViewGroup) view);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    setClickable(vg.getChildAt(i), clickable);
                }
            }
        }
    }
    
}
