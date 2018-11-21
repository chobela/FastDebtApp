package com.appexpress.fastdebt.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "LETTERS";


    // Table columns
    public static final String _ID = "_id";
    public static final String UID = "uid";
    public static final String NAME = "name";
    public static final String TOWN = "town";
    public static final String ADDRESS = "address";
    public static final String AMOUNT = "amount";
    public static final String DOCID = "docid";
    public static final String COMMENT = "comment";

    // Database Information
    static final String DB_NAME = "LETTER_DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + UID + " TEXT NOT NULL, " + NAME + " TEXT, "
            + TOWN + " TEXT, " + ADDRESS + " TEXT, " + AMOUNT + " TEXT, " +  DOCID + " TEXT, " + COMMENT + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getalldata() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

}
