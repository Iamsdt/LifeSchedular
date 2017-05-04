package com.blogspot.shudiptotrafder.lifeschedular.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*******************************************************************************
 * Copyright (c) 2017.
 * Project Name:Life Scheduler
 * Created By Shudipto Trafder
 * The Android Open Source Project
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * LifeScheduler
 * com.blogspot.shudiptotrafder.lifeschedular
 * Created by Shudipto Trafder on 4/16/2017 at 1:10 PM.
 * Don't modify without permission of Shudipto Trafder
 */

class DatabaseHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "tasks.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 2;


    // Constructor
    DatabaseHelper(Context context) {
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
