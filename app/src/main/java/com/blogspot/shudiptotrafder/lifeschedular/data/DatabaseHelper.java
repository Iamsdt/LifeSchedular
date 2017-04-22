package com.blogspot.shudiptotrafder.lifeschedular.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular
 * Created by Shudipto Trafder on 4/16/2017 at 1:10 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "tasks.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 2;


    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + DB_Contract.Entry.TASK_TABLE_NAME + " (" +
                DB_Contract.Entry._ID+ " INTEGER PRIMARY KEY, " +
                DB_Contract.Entry.COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                DB_Contract.Entry.COLUMN_TASK_SOLUTION + " TEXT, " +
                DB_Contract.Entry.COLUMN_TASK_TYPE + " TEXT, " +
                DB_Contract.Entry.COLUMN_TASK_STATUS + " BOOLEAN, " +
                DB_Contract.Entry.COLUMN_TASK_DUE + " BOOLEAN, " +
                DB_Contract.Entry.COLUMN_TASK_TIME + " TEXT, " +
                DB_Contract.Entry.COLUMN_TASK_DATE+ " TEXT, "+
                " UNIQUE (" + DB_Contract.Entry.COLUMN_TASK_NAME + ") ON CONFLICT REPLACE);";


        Log.e("TABLE",CREATE_TABLE);

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_Contract.Entry.TASK_TABLE_NAME);
        onCreate(db);
    }
}
