package com.example.smartarzamas.ui.adminhubnavigation.adminallusers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminAllUsersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AdminAllUsersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}