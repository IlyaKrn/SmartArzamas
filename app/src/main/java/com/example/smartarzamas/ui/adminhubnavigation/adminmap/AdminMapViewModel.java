package com.example.smartarzamas.ui.adminhubnavigation.adminmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AdminMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}