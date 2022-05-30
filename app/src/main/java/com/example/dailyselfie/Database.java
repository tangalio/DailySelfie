package com.example.dailyselfie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }

    public Cursor getData(String sql) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(sql, null);
    }

    public void insertIMG(String name, byte[] img) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "INSERT INTO IMG VALUES(?,?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
        statement.bindString(1, name);
        statement.bindBlob(2, img);
        statement.executeInsert();
    }

    public void deleteIMG(String name) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String sql = "DELETE FROM IMG WHERE NAME= ?";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
        statement.bindString(1, name);
        statement.executeInsert();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
