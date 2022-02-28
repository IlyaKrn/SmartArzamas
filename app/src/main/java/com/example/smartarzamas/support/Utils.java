package com.example.smartarzamas.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartarzamas.R;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

// класс с различными методами

public final class Utils {

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

    public static byte[] getBytesFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    public static Bitmap compressBitmapToIcon(Bitmap bitmap, int size){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        bitmap = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
        return Bitmap.createScaledBitmap(bitmap, size, size, false);
    }
    public static Bitmap getRoundBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, bitmap.getHeight()/2, bitmap.getHeight()/2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
