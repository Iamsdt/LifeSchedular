package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;
import com.blogspot.shudiptotrafder.lifeschedular.fragment.DatePickerFragment;
import com.blogspot.shudiptotrafder.lifeschedular.fragment.TimePickerFragment;

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

        //initialize preferences
        preferences = getSharedPreferences("TimeDate", Context.MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
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
            }
        });

        //set date
        dateTvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTextView(dateTV);
                datePickerFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        addBtn.setText(getString(R.string.addTaskBtnLabel));

    }

    private void buttonFunctionality() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    private void addTask() {
        if (!validateName()) {
            return;
        }

        String taskNameStr = taskNameEt.getText().toString().trim();
        String taskSolStr = taskSolutionEt.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(DB_Contract.Entry.COLUMN_TASK_NAME, taskNameStr);
        values.put(DB_Contract.Entry.COLUMN_TASK_SOLUTION, taskSolStr);
        values.put(DB_Contract.Entry.COLUMN_TASK_TYPE, taskType);
        values.put(DB_Contract.Entry.COLUMN_TASK_TIME, getSelectedTimeStr());
        values.put(DB_Contract.Entry.COLUMN_TASK_DATE, getSelectedDateStr());
        values.put(DB_Contract.Entry.COLUMN_TASK_STATUS, false);
        values.put(DB_Contract.Entry.COLUMN_TASK_DUE, false);

        Uri uri = getContentResolver().insert(DB_Contract.Entry.CONTENT_URI, values);

        if (uri != null) {
            Toast.makeText(AddTaskActivity.this, "DATA INSERTED URI: " + uri, Toast.LENGTH_SHORT).show();

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
