package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.lifeschedular.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;
import com.blogspot.shudiptotrafder.lifeschedular.utilities.Utility;

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

public class TaskType extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 21;
    private String taskTypeStr;
    private CustomCursorAdapter adapter;

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     */
    private static void sle(String message) {

        final String TAG = "TaskType";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     * @param t       throwable that's show on log
     */

    private static void slet(String message, Throwable t) {

        final String TAG = "TaskType";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, t);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        taskTypeStr = intent.getStringExtra(Intent.EXTRA_TEXT);
        toolbar.setTitle(getString(R.string.title_task_type, taskTypeStr));
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.task_type_recycler_view);

        adapter = new CustomCursorAdapter(this);

        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //get task id from viewHolder that's we put in our recyclerView adapter
                int id = (int) viewHolder.itemView.getTag();

                //build a uri
                Uri uri = DB_Contract.Entry.buildUriWithID(id);

                ContentValues values = new ContentValues();
                int update;
                if (taskTypeStr.equalsIgnoreCase(MainActivity.DUE)) {
                    values.put(DB_Contract.Entry.COLUMN_TASK_DUE, false);
                    update = getContentResolver().update(uri, values, null, null);

                } else {
                    values.put(DB_Contract.Entry.COLUMN_TASK_STATUS, true);
                    update = getContentResolver().update(uri, values, null, null);
                }

                if (update > 0) {
                    sle("Successful");
                }

                //getContentResolver().delete(uri,null,null);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, TaskType.this);

            }
        }).attachToRecyclerView(recyclerView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.task_type_fab);

        //if we are in due type task then we don't need to add new task
        if (taskTypeStr.equalsIgnoreCase(MainActivity.DUE)) {
            fab.setVisibility(View.GONE);

        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addTaskIntent = new Intent(TaskType.this, AddTaskActivity.class);
                    addTaskIntent.putExtra(Intent.EXTRA_TEXT, taskTypeStr);
                    startActivity(addTaskIntent);
                }
            });
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor cursorTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {

                if (cursorTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(cursorTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {

                try {

                    //new String[]{"0"} here 0 for false value because
                    // we insert task status column type as boolean
                    //if status is true then store 1 and opposite store 0

                    if (taskTypeStr.equalsIgnoreCase(MainActivity.DUE)) {
                        String selection = DB_Contract.Entry.COLUMN_TASK_DUE + " = ?"
                                + " AND " + DB_Contract.Entry.COLUMN_TASK_TYPE + " =? ";

                        return getContentResolver().query(DB_Contract.Entry.CONTENT_URI, null,
                                selection, new String[]{"1", taskTypeStr}, null);
                    } else {
                        String selection = DB_Contract.Entry.COLUMN_TASK_STATUS + " = ?"
                                + " AND " + DB_Contract.Entry.COLUMN_TASK_TYPE + " =? ";

                        return getContentResolver().query(DB_Contract.Entry.CONTENT_URI, null,
                                selection, new String[]{"0", taskTypeStr}, null);
                    }

                } catch (Exception e) {
                    slet("Database query failed on task type",e);
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                cursorTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.getCount() > 0) {
            adapter.swapCursor(data);
        } else {
            //TODO ADD some picture with
            View view = findViewById(R.id.notTaskFound);
            view.setVisibility(View.VISIBLE);
            TextView textView = (TextView) findViewById(R.id.noExistsTv);
            assert textView != null;
            textView.setTextSize(Utility.getTextSize(this));
            switch (taskTypeStr) {
                case MainActivity.EVERYDAY:
                    textView.setText(getString(R.string.noExistsEveryday));
                    break;

                case MainActivity.TODAY:
                    textView.setText(getString(R.string.noExistsToday));
                    break;

                case MainActivity.SCHEDULE:
                    textView.setText(getString(R.string.noExistsSchedule));
                    break;

                case MainActivity.DUE:
                    textView.setText(getString(R.string.noExistsDue));
                    break;
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
