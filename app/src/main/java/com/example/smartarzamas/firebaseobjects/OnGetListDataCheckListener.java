package com.example.smartarzamas.firebaseobjects;

import java.util.ArrayList;

public interface OnGetListDataCheckListener<T> {
    void onGetData(ArrayList<T> data);
    void onVoidData();
    void onNoConnection();
    void onCanceled();
}
