package com.test.myfirstandroidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDb extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "users.db";
    public static final String TABLE_NAME = "Users";
    public static final String COL1 = "UserId";
    public static final String COL2 = "Email";
    public static final String COL3 = "FirstName";
    public static final String COL4 = "LastName";
    public static final String COL5 = "Phone";

    public SQLiteDb(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /* Code runs automatically when the dB is created */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "+ TABLE_NAME +" ('UserId' INTEGER PRIMARY KEY AUTOINCREMENT ,'Email' TEXT NOT NULL UNIQUE,'FirstName' TEXT NOT NULL,'LastName' TEXT NOT NULL,'Phone' TEXT)";
        db.execSQL(createTable);
    }

    /* Every time the dB is updated (or upgraded) */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /* Basic function to add data. REMEMBER: The fields
       here, must be in accordance with those in
       the onCreate method above.
    */
    public boolean AddUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, user.getEmailAddress());
        contentValues.put(COL3, user.getFirstName());
        contentValues.put(COL4, user.getLastName());
        contentValues.put(COL5, user.getPhoneNumber());

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if data are inserted incorrectly, it will return -1
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean UpdateUser(int id,User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, user.getEmailAddress());
        contentValues.put(COL3, user.getFirstName());
        contentValues.put(COL4, user.getLastName());
        contentValues.put(COL5, user.getPhoneNumber());

        long result = db.update(TABLE_NAME,contentValues,"UserId = ?",new String[] {""+id});

        if(result == -1)
            return false;
        else
            return true;
    }

    public void DeleteUser(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor result = db.rawQuery("DELETE FROM "+TABLE_NAME+" WHERE UserId="+id,null);
        result.moveToFirst();
    }

    // Return everything inside the dB
    public Cursor GetAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor users = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return users;
    }

    public User GetUser(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME+" WHERE UserId="+id,null);
            data.moveToFirst();
            if(data!=null)
                return new User(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4));
            else
                return null;
        }
        catch (Exception e){
            return null;
        }
    }

    /* Returns only one result */
    public Cursor GetUserFromDifferentTable(int ID) {
        SQLiteDatabase db = this.getReadableDatabase(); // No need to write
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL1,
                        COL2, COL3}, COL1 + "=?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }
}
