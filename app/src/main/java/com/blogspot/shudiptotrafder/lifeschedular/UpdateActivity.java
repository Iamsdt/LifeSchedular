package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;
import com.blogspot.shudiptotrafder.lifeschedular.fragment.DatePickerFragment;
import com.blogspot.shudiptotrafder.lifeschedular.fragment.TimePickerFragment;

public class UpdateActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 25;
    private Uri uri;
    //for ui
    private EditText taskNameEt, taskSolutionEt;
    private TextView dateTV;
    private TextView timeTV;
    private Button updateBtn;


    private String dateStrFromDB, timeStrFromDB;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        uri = intent.getData();

        if (uri == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        //assign view
        assignAllView();

        preferences = getSharedPreferences("TimeDate", Context.MODE_PRIVATE);

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

        buttonFunctionality();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void assignAllView() {
        //button
        updateBtn = (Button) findViewById(R.id.taskSubmitBtn);
        //editText
        taskNameEt = (EditText) findViewById(R.id.addTaskNameEt);
        taskSolutionEt = (EditText) findViewById(R.id.addTaskSolEt);
        //textView
        dateTV = (TextView) findViewById(R.id.dateLayDate);
        TextView dateTvLabel = (TextView) findViewById(R.id.dateLayLabel);
        timeTV = (TextView) findViewById(R.id.timeLayTime);
        TextView timeTvLabel = (TextView) findViewById(R.id.timeLayLabel);

        //setTextView
        dateTvLabel.setText(getString(R.string.updateHintDate));
        timeTvLabel.setText(getString(R.string.updateHintTime));

        //set time
        timeTvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment pickerDialog = new TimePickerFragment();
                pickerDialog.setTextView(timeTV);
                pickerDialog.setTimeStr(timeStrFromDB);
                pickerDialog.show(getFragmentManager(), "Time Pick");
            }
        });

        //set date
        dateTvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTextView(dateTV);
                datePickerFragment.setDateStr(dateStrFromDB);
                datePickerFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        updateBtn.setText(getString(R.string.updateTaskBtnLabel));

    }

    private void buttonFunctionality() {
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskNameStr = taskNameEt.getText().toString().trim();
                String taskSolStr = taskSolutionEt.getText().toString().trim();

                ContentValues values = new ContentValues();
                values.put(DB_Contract.Entry.COLUMN_TASK_NAME, taskNameStr);
                values.put(DB_Contract.Entry.COLUMN_TASK_SOLUTION, taskSolStr);
                values.put(DB_Contract.Entry.COLUMN_TASK_TIME, getSelectedTimeStr());
                values.put(DB_Contract.Entry.COLUMN_TASK_DATE, getSelectedDateStr());

                int update = getContentResolver().update(uri, values, null, null);

                if (update > 0) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
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
                return getContentResolver().query(uri, null, null, null, null);
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

        String nameStr, solutionStr, dateStr, timeStr;

        data.moveToFirst();

        do {

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

        taskNameEt.setText(nameStr);

        if (solutionStr == null) {
            taskSolutionEt.setVisibility(View.GONE);
        } else {
            taskSolutionEt.setText(solutionStr);
        }

        if (dateStr == null) {
            dateTV.setVisibility(View.GONE);
        } else {
            dateTV.setText(dateStr);
        }

        if (timeStr == null) {
            timeTV.setVisibility(View.GONE);
        } else {
            timeTV.setText(timeStr);
        }

        dateStrFromDB = dateStr;
        timeStrFromDB = timeStr;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public String getSelectedDateStr() {
        return preferences.getString("Date", null);
    }

    public String getSelectedTimeStr() {
        return preferences.getString("Time", null);
    }
}
