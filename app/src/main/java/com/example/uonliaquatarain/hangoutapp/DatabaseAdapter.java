package com.example.uonliaquatarain.hangoutapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter  {

    DatabaseHelper databaseHelper;
    public DatabaseAdapter(Context context){
        databaseHelper = new DatabaseHelper(context);
    }

    public long insertData(String name, String username, int isActive){
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.USERNAME, username);
        contentValues.put(DatabaseHelper.IS_ACTIVE, isActive);
        long id = sqLiteDatabase.insert(DatabaseHelper.USERS_TABLE, null, contentValues);
        return id;
    }

    public void set_isActive(String username, int set){
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.IS_ACTIVE, set);
        String[] whereArgs = {username};
        if(set == 0) {
            sqLiteDatabase.update(DatabaseHelper.USERS_TABLE, contentValues, DatabaseHelper.USERNAME + " !=?", whereArgs);
        }
        else if(set == 1){
            sqLiteDatabase.update(DatabaseHelper.USERS_TABLE, contentValues, DatabaseHelper.USERNAME + " =?", whereArgs);
        }
    }

    public List<String> getData(){
        List<String> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.NAME, DatabaseHelper.USERNAME};
        String selection = DatabaseHelper.IS_ACTIVE + " = 1";
        Cursor cursor =  sqLiteDatabase.query(DatabaseHelper.USERS_TABLE, columns, selection, null, null, null, null);
        while(cursor.moveToNext()){
            int[] index = new int[2];
            index[0] = cursor.getColumnIndex(DatabaseHelper.NAME);
            index[1] = cursor.getColumnIndex(DatabaseHelper.USERNAME);
            String name = cursor.getString(index[0]);
            String username = cursor.getString(index[1]);
            list.add(name);
            list.add(username);
        }
        return list;
    }

    public Boolean savePic(String username, String imagePath){
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        try {
            FileInputStream fileInputStream = new FileInputStream(imagePath);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.PROFILE_PIC, bytes);
            String[] whereArgs = {username};
            sqLiteDatabase.update(DatabaseHelper.USERS_TABLE, contentValues, DatabaseHelper.USERNAME + " =?", whereArgs);
            fileInputStream.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
//
//    public Bitmap getPic(String userrname){
//        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
//        Bitmap bitmap = null;
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM ")
//    }




    static class DatabaseHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME = "HangoutDatabase";
        private static final String USERS_TABLE = "Users";
        private static final int DATABASE_VERSION = 1;
        private static final String UID = "_id";
        private static final String NAME = "Name";
        private static final String USERNAME = "Username";
        private static final String IS_ACTIVE = "IsActive";
        private static final String PROFILE_PIC= "ProfilePic";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                String query =
                        "CREATE TABLE " + USERS_TABLE + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + NAME + " VARCHAR(45), " + USERNAME+ " VARCHAR(45), " + IS_ACTIVE + " INTEGER, " + PROFILE_PIC + " BLOB );";
                db.execSQL(query);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                String query = "DROP TABLE IF EXISTS " + USERS_TABLE + "; ";
                db.execSQL(query);
                onCreate(db);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
