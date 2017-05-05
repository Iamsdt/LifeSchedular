package com.blogspot.shudiptotrafder.lifeschedular.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.blogspot.shudiptotrafder.lifeschedular.MainActivity;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

import java.util.ArrayList;

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

class EverydayTaskSync extends AsyncTask<Void, Void, Void> {

    private final Context context;

    EverydayTaskSync(Context context) {
        this.context = context;
    }


    /**
     * In this methods we set everyday finished  task to again unfinished
     * we query only everyday type task
     * first we query everyday type task and it's status
     * add save those ids into a array
     * <p>
     * after that we get ids from array and build a uri
     * through uri we update
     * task type into due
     * task status into false
     *
     * @param params we don't need any thing return
     **/

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
        //after async task finished we get a notification
        //but this is testing purpose only
        TaskNotification.notify(context, "Everyday task syn: ", 2);
    }
}
