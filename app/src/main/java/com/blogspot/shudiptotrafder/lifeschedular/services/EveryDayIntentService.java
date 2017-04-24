package com.blogspot.shudiptotrafder.lifeschedular.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.blogspot.shudiptotrafder.lifeschedular.MainActivity;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular.services
 * Created by Shudipto Trafder on 4/24/2017 at 5:02 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class EveryDayIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    public EveryDayIntentService() {
        super("EveryDayIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        syncWeather(this);

    }

    synchronized public static void syncWeather(Context context) {
        String selection = DB_Contract.Entry.COLUMN_TASK_TYPE +" =?"
                +" AND "+ DB_Contract.Entry.COLUMN_TASK_STATUS +" =?";

        String[] selectionArg = new String[]{MainActivity.EVERYDAY,"0"};

        Cursor cursor = context.getContentResolver().query(
                DB_Contract.Entry.CONTENT_URI,
                null,
                selection,
                selectionArg,
                null);

        if (cursor != null) {

            do {
                int id = cursor.getInt(cursor.getColumnIndex(DB_Contract.Entry._ID));
                Uri uri = DB_Contract.Entry.buildUriWithID(id);

                ContentValues values = new ContentValues();
                values.put(DB_Contract.Entry.COLUMN_TASK_STATUS,false);

                context.getContentResolver().update(uri,values,null,null);

            } while (cursor.moveToNext());
        }

        if (cursor != null){
            cursor.close();
        }
    }

}
