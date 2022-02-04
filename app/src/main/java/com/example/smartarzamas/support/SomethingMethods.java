package com.example.smartarzamas.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartarzamas.R;

import java.util.Locale;

// класс с различными методами

public class SomethingMethods {

    // получение строки с текущей датой
    // например 12.12.12 12:12
    public static String getDateString(){
        //  Date date = new Date();
        //String dateString = String.valueOf(date);
        //Log.e(null, dateString);
        return "";// dateString;
    }
    // проверка наличия подключения к интернету
    private static boolean hasConnection(final Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;

    }
    // метод для преобразования строки (эл. почты) в строку, которую можно записать в firebase
    public static String getKeyString(String email){
        String key = "";
        char[] arr = email.toCharArray();
        for (int i = 0; i < arr.length-1; i++){
            key += (int) arr[i] + "_";
        }
        key += (int) arr[arr.length-1];
        return key;
    }
    //  проверка наличия подключения к интернету с выведением предупреждения в случае его отсутствия
    // если подключение есть, то выполняется метод task.isConnected()
    public static void isConnected(Context applicationContext, Connection task) {
        if (hasConnection(applicationContext)){
            task.isConnected();
        }
        else {
            Toast.makeText(applicationContext, R.string.review_connection, Toast.LENGTH_LONG).show();
        }
    }
    // методы для отображения и скрытия TextView с предупреждениями
    public static void showWarning(TextView textView, int resource){
        textView.setVisibility(View.VISIBLE);
        textView.setText(resource);
    }
    public static void hideWarning(TextView ... textViews){
        for (TextView textView : textViews) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.none);
        }
    }
    // проверка близкого совпадения строк
    public static boolean isEquals(String sample, String child){
        if (child == null)
            child = "";
        if (sample == null)
            sample = "";

        sample = sample.toLowerCase(Locale.ROOT);
        child = child.toLowerCase(Locale.ROOT);

        if (sample.equals(""))
            return true;
        if (sample.equals(child)){
            return true;
        }
        else {
            return false;
        }

    }
    // интерфейс подключения
    public interface Connection{
        void isConnected();
    }
}
