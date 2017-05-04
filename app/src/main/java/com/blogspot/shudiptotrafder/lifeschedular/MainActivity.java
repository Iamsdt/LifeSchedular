package com.blogspot.shudiptotrafder.lifeschedular;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.lifeschedular.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;
import com.blogspot.shudiptotrafder.lifeschedular.manager.TaskManager;
import com.blogspot.shudiptotrafder.lifeschedular.settings.SettingActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Calendar;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //All task type
    public static final String EVERYDAY = "Everyday";
    public static final String TODAY = "Today";
    public static final String SCHEDULE = "Schedule";
    public static final String DUE = "Due";
    //loaders id
    private static final int TASK_LOADER_ID = 44;
    RecyclerView mRecyclerView;
    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     */
    private static void sle(String message) {

        final String TAG = "MainActivity";

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

        final String TAG = "MAIN_ACTIVITY";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, t);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

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

                //put those values to database
                //set status to true that's means task has been done
                ContentValues values = new ContentValues();
                values.put(DB_Contract.Entry.COLUMN_TASK_STATUS, true);
                int update = getContentResolver().update(uri, values, null, null);

                //if failed update value will be -1
                if (update > 0) {
                    sle("Successful");
                }

                //value has changed
                //so restart the adapter
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        //all fab function
        fabFunctionality();

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void fabFunctionality (){

        //all fab button
        final FloatingActionButton everydayFab, todayFab, scheduleFab;

        //fab menu
        final FloatingActionMenu fabMenu =  (FloatingActionMenu) findViewById(R.id.main_fab_menu);

        everydayFab = (FloatingActionButton) findViewById(R.id.main_fab_everyday);
        todayFab = (FloatingActionButton) findViewById(R.id.main_fab_today);
        scheduleFab = (FloatingActionButton) findViewById(R.id.main_fab_schedule);

        everydayFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchAddTaskActivity(EVERYDAY);
                fabMenu.close(true);
            }
        });

        todayFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchAddTaskActivity(TODAY);
                fabMenu.close(true);
            }
        });

        scheduleFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchAddTaskActivity(SCHEDULE);
                fabMenu.close(true);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        sle("Set time: " + String.valueOf(calendar.getTime() + " Month: " + calendar.get(Calendar.DAY_OF_MONTH)));

        //alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, TaskManager.class);
        intent.setAction("com.blogspot.shudiptotrafder.lifeschedular");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        ComponentName receiver = new ComponentName(this, TaskManager.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void launchAddTaskActivity(String tasKType) {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, tasKType);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_task) {
            startActivity(new Intent(this, AddTaskActivity.class));

        } else if (id == R.id.nav_everyday){
            gotoTaskTypeActivity(EVERYDAY);

        } else if (id == R.id.nav_today){
            gotoTaskTypeActivity(TODAY);

        } else if (id == R.id.nav_schedule){
            gotoTaskTypeActivity(SCHEDULE);

        } else if (id == R.id.nav_due){
            gotoTaskTypeActivity(DUE);

        } else if (id == R.id.nav_finished) {
            startActivity(new Intent(this, FinishedActivity.class));

        } else if (id == R.id.nav_settings){
            startActivity(new Intent(this, SettingActivity.class));

        } else if (id == R.id.nav_share){
            share();

        } else if (id == R.id.nav_about){
            //startActivity(new Intent(this,AboutActivity.class));
            about();

        } else if (id == R.id.nav_developer){
            startActivity(new Intent(this, DeveloperActivity.class));

        } else if (id == R.id.nav_feedback) {
            feedback();

        } else if (id == R.id.nav_licence){
            startActivity(new Intent(this, LicenceActivity.class));

        } else if (id == R.id.nav_copy_right) {
            alertDialog();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.aboutLabel);
        builder.setMessage(R.string.about);
        builder.setPositiveButton("ok", null);
        builder.setNegativeButton("cancel", null);
        builder.show();
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.copyrightTitle);
        builder.setMessage(R.string.copyright);
        builder.setPositiveButton("ok", null);
        builder.setNegativeButton("cancel", null);
        builder.show();
    }

    private void feedback() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText name = (EditText) dialogView.findViewById(R.id.customName);
        final EditText email = (EditText) dialogView.findViewById(R.id.customEmail);
        final EditText message = (EditText) dialogView.findViewById(R.id.customFeedback);

        dialogBuilder.setTitle("Send FeedBack");
        dialogBuilder.setMessage("please send me to your feedback.");
        dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nameStr = name.getText().toString().trim();
                String emailStr = email.getText().toString();
                String messageStr = message.getText().toString().trim();

                //TODO fix email

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Shudiptotrafder@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_CC, emailStr);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Life Scheduler Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, nameStr + ":" + messageStr);

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                } else {
                    Toast.makeText(MainActivity.this, "Sorry you don't have any email app", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    //share option
    private void share() {
        String mimType = "text/plain";
        String title = "Life Scheduler";
        String textToShare = "Hey! you can use this helpful app";

        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimType)
                .setText(textToShare)
                .startChooser();
    }

//    private void dummyToast(){
//        Toast.makeText(this, "It will be available on beta 4 or 5 version", Toast.LENGTH_SHORT).show();
//    }

    private void gotoTaskTypeActivity(String taskType){
        Intent intent = new Intent(MainActivity.this,TaskType.class);
        intent.putExtra(Intent.EXTRA_TEXT,taskType);
        startActivity(intent);
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
                    return getContentResolver().query(DB_Contract.Entry.CONTENT_URI, null,
                            DB_Contract.Entry.COLUMN_TASK_STATUS + " =? ", new String[]{"0"}, null);
                } catch (Exception e) {
                    slet("Database query failed", e);
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


    /*replaced by another option of recycle view click listener
    @Override
    public void onClickListener(String taskName) {
        Toast.makeText(this, "Click: " + String.valueOf(taskName), Toast.LENGTH_SHORT).show();
    } */

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
