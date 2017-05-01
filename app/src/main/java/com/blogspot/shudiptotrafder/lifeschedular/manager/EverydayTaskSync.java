package com.blogspot.shudiptotrafder.lifeschedular.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.blogspot.shudiptotrafder.lifeschedular.MainActivity;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

import java.util.ArrayList;

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular.manager
 * Created by Shudipto Trafder on 4/27/2017 at 2:05 PM.
 * Don't modify without permission of Shudipto Trafder
 */

class EverydayTaskSync extends AsyncTask<Void, Void, Void> {

    private Context context;

    EverydayTaskSync(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        ArrayList<Integer> taskIds = new ArrayList<>();

        String selection = DB_Contract.Entry.COLUMN_TASK_TYPE + " =?"
                + " AND " + DB_Contract.Entry.COLUMN_TASK_STATUS + " =?";

        //here we select only true value
        String[] selectionArg = new String[]{MainActivity.EVERYDAY, "1"};

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
                    values.put(DB_Contract.Entry.COLUMN_TASK_STATUS, false);

                    context.getContentResolver().update(uri, values, null, null);

                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
