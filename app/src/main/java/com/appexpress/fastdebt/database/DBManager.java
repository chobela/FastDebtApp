package com.appexpress.fastdebt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public void insert(String uid, String name, String town, String address, String amount, String docid, String comment ) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.UID, uid);
        contentValue.put(DatabaseHelper.NAME, name);
        contentValue.put(DatabaseHelper.TOWN, town);
        contentValue.put(DatabaseHelper.ADDRESS, address);
        contentValue.put(DatabaseHelper.AMOUNT, amount);
        contentValue.put(DatabaseHelper.DOCID, docid);
        contentValue.put(DatabaseHelper.COMMENT, comment);

        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.UID, DatabaseHelper.NAME,
                DatabaseHelper.TOWN, DatabaseHelper.ADDRESS, DatabaseHelper.AMOUNT, DatabaseHelper.DOCID, DatabaseHelper.COMMENT};

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String uid, String name, String town, String address, String amount, String docid, String comment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.UID, uid);
        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.TOWN, town);
        contentValues.put(DatabaseHelper.ADDRESS, address);
        contentValues.put(DatabaseHelper.AMOUNT, amount);
        contentValues.put(DatabaseHelper.DOCID, docid);
        contentValues.put(DatabaseHelper.COMMENT, comment);

        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete () {
        database.delete(DatabaseHelper.TABLE_NAME, null, null);
        database.close();
    }
}
