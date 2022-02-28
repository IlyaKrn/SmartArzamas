package com.example.smartarzamas.firebaseobjects;

import java.util.ArrayList;

public interface OnGetListDataListener<T> {
    void onGetData(ArrayList<T> data);
    void onVoidData();
    void onNoConnection();
    void onCanceled();
}
