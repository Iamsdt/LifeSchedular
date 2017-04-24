package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

public class UpdateActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText nameEt, descriptionEt, dateEt, timeEt;

    private TextView taskTypeTV;

    private Uri uri;
    private static final int LOADER_ID = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //text view
        taskTypeTV = (TextView) findViewById(R.id.updateTaskTV);

        //Edit text
        nameEt = (EditText) findViewById(R.id.updateTaskNameEt);
        descriptionEt = (EditText) findViewById(R.id.updateTaskDesEt);
        dateEt = (EditText) findViewById(R.id.updateTaskDateEt);
        timeEt = (EditText) findViewById(R.id.updateTaskTimeEt);

        //button
        Button updateBtn = (Button) findViewById(R.id.updateButton);

        Intent intent = getIntent();
        uri = intent.getData();

        if (uri == null){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.update_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask();
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    private void updateTask() {

        String nameStr = nameEt.getText().toString();
        String descriptionStr = descriptionEt.getText().toString();
        String dateStr = dateEt.getText().toString();
        String timeStr = timeEt.getText().toString();

        ContentValues values = new ContentValues();
        values.put(DB_Contract.Entry.COLUMN_TASK_NAME, nameStr);
        values.put(DB_Contract.Entry.COLUMN_TASK_SOLUTION, descriptionStr);
        values.put(DB_Contract.Entry.COLUMN_TASK_DATE, dateStr);
        values.put(DB_Contract.Entry.COLUMN_TASK_TIME, timeStr);

        int update = getContentResolver().update(uri, values,null,null);

        if (update > 0) {
            Toast.makeText(this, "Your Task is updated", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor cursor;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (cursor != null) {
                    deliverResult(cursor);

                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                return getContentResolver().query(uri,null,null,null,null);
            }

            @Override
            public void deliverResult(Cursor data) {
                super.deliverResult(data);
                cursor = data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        String taskType,nameStr,solutionStr,dateStr,timeStr;

        data.moveToFirst();

        do {
             taskType = data.getString(data.getColumnIndex(
                    DB_Contract.Entry.COLUMN_TASK_TYPE));
            nameStr = data.getString(data.getColumnIndex(
                    DB_Contract.Entry.COLUMN_TASK_NAME));

            solutionStr = data.getString(data.getColumnIndex(
                    DB_Contract.Entry.COLUMN_TASK_SOLUTION));

            dateStr = data.getString(data.getColumnIndex(
                    DB_Contract.Entry.COLUMN_TASK_DATE));

            timeStr = data.getString(data.getColumnIndex(
                    DB_Contract.Entry.COLUMN_TASK_TIME));


        } while (data.moveToNext());

        //set all

        taskTypeTV.setText(getString(R.string.title_task_type,taskType));

        nameEt.setText(nameStr);

        descriptionEt.setText(solutionStr);

        dateEt.setText(dateStr);

        timeEt.setText(timeStr);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    /**
     * This methods show log error message with throwable
     * @param message String show on log
     * @param t throwable that's show on log
     */

    private static void slet(String message,Throwable t){

        final String TAG = "UpdateActivity";

        if (BuildConfig.DEBUG){
            Log.e(TAG,message,t);
        }
    }
}
