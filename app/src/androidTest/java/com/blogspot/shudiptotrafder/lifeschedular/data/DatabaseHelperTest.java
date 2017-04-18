package com.blogspot.shudiptotrafder.lifeschedular.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular.data
 * Created by Shudipto Trafder on 4/16/2017 at 10:58 PM.
 * Don't modify without permission of Shudipto Trafder
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper helper;

    @Before
    public void setUp() throws IOException {
        getTargetContext().deleteDatabase(DatabaseHelper.DB_NAME);
        helper = new DatabaseHelper(getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        helper.close();
    }

    @Test
    public void insert() throws Exception {

        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("task_name","Shudipto");
        values.put("task_solution","student");
        values.put("task_type","today");
        values.put("task_status","false");
        values.put("task_date","21");
        values.put("task_time","22");

        database.insert("tasks",null,values);

    }

    @Test
    public void query() throws Exception {

        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM tasks;";

        Cursor cursor = db.rawQuery(sql,null);

        cursor.moveToFirst();

        ArrayList<String> a = new ArrayList<>();

        while (cursor.isAfterLast()){
            String text = cursor.getString(cursor.getColumnIndex("task_name"));
            a.add(text);
        }

        Log.e("TEST", "query: ");

        cursor.close();

    }

}