package com.example.smartarzamas.ui.hubnavigation.mychats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyChatsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyChatsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}