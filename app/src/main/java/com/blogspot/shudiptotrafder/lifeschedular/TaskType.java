package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.blogspot.shudiptotrafder.lifeschedular.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class TaskType extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String taskTpyeStr;
    private CustomCursorAdapter adapter;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    private static final int LOADER_ID = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        taskTpyeStr = intent.getStringExtra(Intent.EXTRA_TEXT);
        toolbar.setTitle(getString(R.string.title_task_type, taskTpyeStr));
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.task_type_recycler_view);

        adapter = new CustomCursorAdapter(this);

        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.task_type_fab);
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
                    String selection = DB_Contract.Entry.COLUMN_TASK_STATUS + " = ?"
                            +" AND "+ DB_Contract.Entry.COLUMN_TASK_TYPE + " =? ";

                    return getContentResolver().query(DB_Contract.Entry.CONTENT_URI, null,
                            selection, new String[]{"0",taskTpyeStr}, null);

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
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
    
    /**
     * This methods show log error message with throwable
     * @param message String show on log
     */
    private static void sle(String message){
            
        final String TAG = "TaskType"; 
            
        if (BuildConfig.DEBUG){
            Log.e(TAG,message);
        }
    }
    
    /**
     * This methods show log error message with throwable
     * @param message String show on log
     * @param t throwable that's show on log
     */
    
    private static void slet(String message,Throwable t){
        
        final String TAG = "TaskType";
    
        if (BuildConfig.DEBUG){
            Log.e(TAG,message,t);
        }
    }
}
