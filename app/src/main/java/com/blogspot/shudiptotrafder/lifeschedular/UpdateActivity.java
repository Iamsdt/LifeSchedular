package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;
import com.blogspot.shudiptotrafder.lifeschedular.fragment.DatePickerFragment;
import com.blogspot.shudiptotrafder.lifeschedular.fragment.TimePickerFragment;

import java.util.ArrayList;
import java.util.List;

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

public class UpdateActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 25;
    // Spinner element
    private Spinner spinner;
    private Uri uri;
    //for ui edit text
    private EditText taskNameEt, taskSolutionEt;

    //text view
    private TextView dateTV;
    private TextView timeTV;
    //button
    private Button updateBtn;
    //saved date and time
    private String dateStrFromDB, timeStrFromDB;
    //shared preference for get date and time
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get uri
        Intent intent = getIntent();
        uri = intent.getData();

        //if uri is null then we finish this activity
        if (uri == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

        //assign view
        assignAllView();

        //set sp
        preferences = getSharedPreferences("TimeDate", Context.MODE_PRIVATE);

        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add(MainActivity.EVERYDAY);
        categories.add(MainActivity.TODAY);
        categories.add(MainActivity.SCHEDULE);

        // Creating adapter for spinner

        ArrayAdapter dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //button task
        buttonFunctionality();

        //initialize loader
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
            public void onClick(final View v) {

                //get task name and solution from edit text
                String taskNameStr = taskNameEt.getText().toString().trim();
                String taskSolStr = taskSolutionEt.getText().toString().trim();

                //set on to value
                ContentValues values = new ContentValues();
                values.put(DB_Contract.Entry.COLUMN_TASK_NAME, taskNameStr);
                values.put(DB_Contract.Entry.COLUMN_TASK_SOLUTION, taskSolStr);
                values.put(DB_Contract.Entry.COLUMN_TASK_TIME, getSelectedTimeStr());
                values.put(DB_Contract.Entry.COLUMN_TASK_DATE, getSelectedDateStr());
                if (getTaskType() != null) {
                    values.put(DB_Contract.Entry.COLUMN_TASK_TYPE, getTaskType());
                }

                int update = getContentResolver().update(uri, values, null, null);

                //if update success full then finish this activity
                if (update > 0) {
                    finish();
                }
            }
        });
    }

    //get task type from spinner
    private String getTaskType() {

        final String[] tasks = new String[]{null};

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tasks[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        return tasks[0];
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

    private String getSelectedDateStr() {
        return preferences.getString("Date", null);
    }

    private String getSelectedTimeStr() {
        return preferences.getString("Time", null);
    }
}
