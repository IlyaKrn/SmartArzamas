package com.example.smartarzamas.SQLiteDatabase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SQLiteDbManager {
    
    private Context context;  // контекст для SQLiteDbHelper
    private static SQLiteDbHelper myDbHelper; // объект для взаимодейсевия с бд
    private static SQLiteDatabase db; // база данных
    private OnDataChangeListener onDataChange; // интерфейс для обновления данных в активности

    // конструктор
    public SQLiteDbManager(Context context){
        this.context = context;
        myDbHelper = new SQLiteDbHelper(context);
    }

    // открытие и закрытие бд
    private static void openDb(){
        db = myDbHelper.getWritableDatabase();
    }
    private static void closeDb(){
        myDbHelper.close();
    }
    // запись данных в бд
    public void insertToDb(String login, String password, String id){
        ContentValues cv = new ContentValues();
        cv.put(SQLiteDbConstants.LOGIN, login);
        cv.put(SQLiteDbConstants.PASSWORD, password);
        cv.put(SQLiteDbConstants.ID, id);
        openDb();
        db.insert(SQLiteDbConstants.TABLE_NAME, null, cv);
        closeDb();
        if (onDataChange != null)
            onDataChange.onCurrentUserChange(getCurrentUser());
    }
    // проверка наличия содежимого в бд
    public  boolean isEmpty(){
        openDb();
        Cursor cursor = db.query(SQLiteDbConstants.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            closeDb();
            return false;
        }
        cursor.close();
        return true;

    }
    // получение последней записи из бд
    public SQLUser getCurrentUser(){
        if (!isEmpty())
            return getDbSQLUserList().get(getDbSQLUserList().size()-1);
        else
            return null;
    }
    // получение списка всех запией
    public ArrayList<SQLUser> getDbSQLUserList(){
        openDb();
        ArrayList<SQLUser> list = new ArrayList<>();
        Cursor cursor = db.query(SQLiteDbConstants.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String l = cursor.getString(cursor.getColumnIndex(SQLiteDbConstants.LOGIN));
            @SuppressLint("Range") String p = cursor.getString(cursor.getColumnIndex(SQLiteDbConstants.PASSWORD));
            @SuppressLint("Range") String i = cursor.getString(cursor.getColumnIndex(SQLiteDbConstants.ID));
            list.add(new SQLUser(l, p, i));
        }
        cursor.close();
        closeDb();
        return list;
    }
    // удаление записи по id в бд
    public void deleteNote(String id) {
        openDb();
        db.delete(SQLiteDbConstants.TABLE_NAME, SQLiteDbConstants._ID + " = ?",
            new String[] { String.valueOf(id) });
        db.close();
        if (onDataChange != null)
            onDataChange.onCurrentUserChange(getCurrentUser());
    }
    // удаление всех записей
    public void clear() {
        openDb();
        db.delete(SQLiteDbConstants.TABLE_NAME, null, null);
        closeDb();
        if (onDataChange != null)
            onDataChange.onCurrentUserChange(getCurrentUser());
    }
    // установка слушателя изменений в бд
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        this.onDataChange = onDataChangeListener;
    }
    // интерфейс для обработки изменений в бд
    public interface OnDataChangeListener{
        // onCurrentUserChange(SQLUser currentUser) вызывается при изменении в бд
        public void onCurrentUserChange(SQLUser currentUser);
    }
    // класс, описывающий запись в бд
    public class SQLUser{
        // логин, пароль и id пользователя
        // id должен соответствовать ключу пользователя в firebase

        public String login, password, id;

        // конструктор
        public SQLUser(String login, String password, String id) {
            this.login = login;
            this.password = password;
            this.id = id;
        }
    }

}
