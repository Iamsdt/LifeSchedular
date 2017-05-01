package com.blogspot.shudiptotrafder.lifeschedular.manager;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.blogspot.shudiptotrafder.lifeschedular.MainActivity;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

import java.util.ArrayList;

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular.manager
 * Created by Shudipto Trafder on 4/30/2017 at 8:32 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class DueTaskService extends IntentService {

    public DueTaskService() {
        super("DueTaskService");
    }

    synchronized public static void leftTaskToDue(Context context) {

        ArrayList<Integer> taskIds = new ArrayList<>();

        String selection = DB_Contract.Entry.COLUMN_TASK_STATUS + " =?"
                + " AND " + DB_Contract.Entry.COLUMN_TASK_TYPE + " =?";

        //here we select only true value
        String[] selectionArg = new String[]{"0", MainActivity.TODAY};

        Cursor cursor = context.getContentResolver().query(
                DB_Contract.Entry.CONTENT_URI,
                null,
                selection,
                selectionArg,
                null);


        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            do {

                int id = cursor.getInt(cursor.getColumnIndex(DB_Contract.Entry._ID));
                taskIds.add(id);
            }
            while (cursor.moveToNext());

            if (taskIds.size() > 0) {

                for (int id : taskIds) {
                    Uri uri = DB_Contract.Entry.buildUriWithID(id);
                    ContentValues values = new ContentValues();
                    values.put(DB_Contract.Entry.COLUMN_TASK_STATUS, true);
                    values.put(DB_Contract.Entry.COLUMN_TASK_DUE, true);
                    values.put(DB_Contract.Entry.COLUMN_TASK_TYPE, MainActivity.DUE);

                    context.getContentResolver().update(uri, values, null, null);

                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        leftTaskToDue(this);
    }
}
