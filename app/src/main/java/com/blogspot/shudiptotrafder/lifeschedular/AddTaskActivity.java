package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

public class AddTaskActivity extends AppCompatActivity {

    //Edit text
    private EditText taskNameEt, taskSolutionEt;
    //TextInputLayout
    private TextInputLayout taskNameLayout;
    //date tv
    private TextView dateTV;
    //time tv
    private TextView timeTV;
    //add button to add data to database
    private Button addBtn;

    //type of task add in database
    private String taskType;

    //shared preference
    private SharedPreferences preferences;

    //shared preference date and time
    private String spDate, spTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //assign view
        assignAllView();

        //from intent we need to task type
        //we parse is from main activity
        Intent intent = getIntent();
        taskType = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (taskType == null) {
            setSpinner();
        }

        //initialize preferences
        preferences = getSharedPreferences("TimeDate", Context.MODE_PRIVATE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        buttonFunctionality();
    }

    private void assignAllView() {
        //button
        addBtn = (Button) findViewById(R.id.taskSubmitBtn);
        //editText
        taskNameEt = (EditText) findViewById(R.id.addTaskNameEt);
        taskSolutionEt = (EditText) findViewById(R.id.addTaskSolEt);
        //text input layout
        taskNameLayout = (TextInputLayout) findViewById(R.id.addTaskNameLayout);
        //textView
        dateTV = (TextView) findViewById(R.id.dateLayDate);
        TextView dateTvLabel = (TextView) findViewById(R.id.dateLayLabel);
        timeTV = (TextView) findViewById(R.id.timeLayTime);
        TextView timeTvLabel = (TextView) findViewById(R.id.timeLayLabel);

        //setTextView
        dateTvLabel.setText(getString(R.string.addHintDate));
        timeTvLabel.setText(getString(R.string.addHintTime));

        //set time
        timeTvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment pickerDialog = new TimePickerFragment();
                pickerDialog.setTextView(timeTV);
                pickerDialog.show(getFragmentManager(), "Time Pick");
                spTime = preferences.getString("Time", null);
            }
        });

        //set date
        dateTvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTextView(dateTV);
                datePickerFragment.show(getFragmentManager(), "Date Picker");
                spDate = preferences.getString("Date", null);
            }
        });

        addBtn.setText(getString(R.string.addTaskBtnLabel));

    }

    private void setSpinner() {

        final View view = findViewById(R.id.addTaskSpinner);
        view.setVisibility(View.VISIBLE);

        // Spinner element
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taskType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                requestFocus(spinner);
                Snackbar.make(view, "please select a task type", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add(MainActivity.EVERYDAY);
        categories.add(MainActivity.TODAY);
        categories.add(MainActivity.SCHEDULE);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    private void buttonFunctionality() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(v);
            }
        });
    }

    private void addTask(View view) {

        if (!validateName()) {
            return;
        }
        if (taskType == null) {
            return;
        }

        String taskNameStr = taskNameEt.getText().toString().trim();
        String taskSolStr = taskSolutionEt.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(DB_Contract.Entry.COLUMN_TASK_NAME, taskNameStr);
        values.put(DB_Contract.Entry.COLUMN_TASK_SOLUTION, taskSolStr);
        values.put(DB_Contract.Entry.COLUMN_TASK_TYPE, taskType);
        values.put(DB_Contract.Entry.COLUMN_TASK_TIME, spTime);
        values.put(DB_Contract.Entry.COLUMN_TASK_DATE, spDate);
        values.put(DB_Contract.Entry.COLUMN_TASK_STATUS, false);
        values.put(DB_Contract.Entry.COLUMN_TASK_DUE, false);

        Uri uri = getContentResolver().insert(DB_Contract.Entry.CONTENT_URI, values);

        if (uri != null) {
            Snackbar.make(view, taskType + " type task added", Snackbar.LENGTH_SHORT).show();
            taskNameEt.setText("");
            taskSolutionEt.setText("");

            dateTV.setText("");
            timeTV.setText("");
        }
    }

    private boolean validateName() {
        String taskName = taskNameEt.getText().toString().trim();

        if (taskName.isEmpty() || taskName.length() < 2) {
            taskNameLayout.setError("Please enter task name");
            requestFocus(taskNameEt);
            return false;
        } else {
            taskNameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public String getSelectedDateStr() {
        return preferences.getString("Date", null);
    }

    public String getSelectedTimeStr() {
        return preferences.getString("Time", null);
    }

}
