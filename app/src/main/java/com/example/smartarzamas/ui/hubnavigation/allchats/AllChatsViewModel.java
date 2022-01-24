package com.example.smartarzamas.ui.hubnavigation.allchats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllChatsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllChatsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is allchats fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}