package com.example.smartarzamas.ui.adminhubnavigation.adminallchats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminAllChatsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AdminAllChatsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}