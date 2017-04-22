package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

public class AddTaskActivity extends AppCompatActivity {

    EditText nameet,solution,date,time;
    Button add;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //assign view
        nameet = (EditText) findViewById(R.id.name);
        solution = (EditText) findViewById(R.id.solution);
        date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        add = (Button) findViewById(R.id.add);
        textView = (TextView) findViewById(R.id.addTV);

        Intent intent = getIntent();
        final String taskType = intent.getStringExtra(Intent.EXTRA_TEXT);

        textView.setText(String.format("Task type: %s", taskType.toUpperCase()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameet.getText().toString();
                String sol = solution.getText().toString();
                String dateStr = date.getText().toString();
                String timeStr = time.getText().toString();

                //for dummy
//                String name = "Shudipto";
//                String sol = "student";
//                String dateStr = "21";
//                String timeStr = "21";
//                String typeStr = "today";

                ContentValues values = new ContentValues();
                values.put(DB_Contract.Entry.COLUMN_TASK_NAME,name);
                values.put(DB_Contract.Entry.COLUMN_TASK_SOLUTION,sol);
                values.put(DB_Contract.Entry.COLUMN_TASK_TYPE,taskType);
                values.put(DB_Contract.Entry.COLUMN_TASK_TIME,dateStr);
                values.put(DB_Contract.Entry.COLUMN_TASK_DATE,timeStr);
                values.put(DB_Contract.Entry.COLUMN_TASK_STATUS,false);
                values.put(DB_Contract.Entry.COLUMN_TASK_DUE,false);

                Uri uri = getContentResolver().insert(DB_Contract.Entry.CONTENT_URI,values);

                if (uri != null) {
                    Toast.makeText(AddTaskActivity.this, "DATA INSERTED URI: "+uri, Toast.LENGTH_SHORT).show();
                    nameet.setText("");
                    solution.setText("");
                    date.setText("");
                    time.setText("");
                }

            }
        });
    }
}
