package com.blogspot.shudiptotrafder.lifeschedular;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.lifeschedular.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

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

public class FinishedActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    //loaders id
    private static final int LOADER_ID = 123;
    //adapter
    private CustomCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.finishedRec);

        adapter = new CustomCursorAdapter(this);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

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

                int delete = getContentResolver().delete(uri, null, null);

                if (delete > 0) {
                    Snackbar.make(viewHolder.itemView, "you delete one finished task", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    getSupportLoaderManager().restartLoader(LOADER_ID, null, FinishedActivity.this);
                }
            }
        }).attachToRecyclerView(recyclerView);


        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
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

            @Override
            public Cursor loadInBackground() {

                String selection = DB_Contract.Entry.COLUMN_TASK_STATUS + "=?"
                        + " AND " + DB_Contract.Entry.COLUMN_TASK_DUE + "=?";
                //select where task status true and due false;
                String[] selectionArg = new String[]{"1", "0"};

                return getContentResolver().query(DB_Contract.Entry.CONTENT_URI,
                        null, selection, selectionArg, DB_Contract.Entry._ID);
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
            View view = findViewById(R.id.finishedNoTaskLayout);
            view.setVisibility(View.VISIBLE);
            TextView textView = (TextView) findViewById(R.id.noExistsTv);
            textView.setText(getString(R.string.noExistsFinished));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
