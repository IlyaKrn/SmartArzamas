package com.example.smartarzamas.firebaseobjects;

public interface OnSetDataListener<T extends FirebaseObject> {
    void onSetData(T data);
    void onNoConnection();
    void onCanceled();

}
