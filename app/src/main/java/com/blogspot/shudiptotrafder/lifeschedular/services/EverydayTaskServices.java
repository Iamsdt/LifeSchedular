package com.blogspot.shudiptotrafder.lifeschedular.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.blogspot.shudiptotrafder.lifeschedular.MainActivity;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * LifeScheduler
 * com.blogspot.shudiptotrafder.lifeschedular
 * Created by Shudipto Trafder on 4/24/2017 at 3:26 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class EverydayTaskServices extends JobService {

    private AsyncTask<Void,Void,Void> everydayTask;

    @Override
    public boolean onStartJob(final JobParameters job) {

        everydayTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                String selection = DB_Contract.Entry.COLUMN_TASK_TYPE +" =?"
                        +" AND "+ DB_Contract.Entry.COLUMN_TASK_STATUS +" =?";

                String[] selectionArg = new String[]{MainActivity.EVERYDAY,"0"};

                Cursor cursor = getContentResolver().query(
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

                        getContentResolver().update(uri,values,null,null);

                    } while (cursor.moveToNext());
                }

                if (cursor != null){
                    cursor.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                jobFinished(job,false);
            }
        };

        everydayTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        if (everydayTask != null) {
            everydayTask.cancel(true);
        }

        return true;
    }

}
