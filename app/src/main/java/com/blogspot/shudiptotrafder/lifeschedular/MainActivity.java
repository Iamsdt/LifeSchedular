package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.blogspot.shudiptotrafder.lifeschedular.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //loaders id
    private static final int TASK_LOADER_ID = 44;

    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;
    RecyclerView mRecyclerView;

    //All Floating button
    FloatingActionButton mainFab, todayFab, everydayFab, scheduleFab;

    //All animator
    Animation fabOpen, fabClose, rotateClockwise, rotateAntiClockwise;

    //for fab is open or closed
    boolean isFabOpen = false;

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

                int id = (int) viewHolder.itemView.getTag();

                String idTag = Integer.toString(id);

                Uri uri = DB_Contract.Entry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(idTag).build();

                ContentValues values = new ContentValues();
                values.put(DB_Contract.Entry.COLUMN_TASK_STATUS, true);
                int update = getContentResolver().update(uri, values, null, null);

                if (update > 0) {
                    sle("Successful");
                }

                //getContentResolver().delete(uri,null,null);

                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        mainFab = (FloatingActionButton) findViewById(R.id.main_fab);
        todayFab = (FloatingActionButton) findViewById(R.id.main_fab_today);
        everydayFab = (FloatingActionButton) findViewById(R.id.main_fab_everyday);
        scheduleFab = (FloatingActionButton) findViewById(R.id.main_fab_schedule);

        //load all animation from xml
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        rotateAntiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anit_clockwise);

        //after that's bind all view and load animation
        //set main fab clock listener
        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabFunctionality();
            }
        });

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

    private void fabFunctionality() {

        if (isFabOpen) {
            //set animation to all view
            //set close fab because fab are closing
            todayFab.setAnimation(fabClose);
            everydayFab.setAnimation(fabClose);
            scheduleFab.setAnimation(fabClose);
            //rotate main fab
            mainFab.setAnimation(rotateAntiClockwise);

            //set all view to click able
            todayFab.setClickable(false);
            scheduleFab.setClickable(false);
            everydayFab.setClickable(false);

            //set all view to gone because view are close
            todayFab.setVisibility(View.GONE);
            scheduleFab.setVisibility(View.GONE);
            everydayFab.setVisibility(View.GONE);

            //at last set isFabOpen is false
            isFabOpen = false;

        } else {

            //set all view to visible
            todayFab.setVisibility(View.VISIBLE);
            scheduleFab.setVisibility(View.VISIBLE);
            everydayFab.setVisibility(View.VISIBLE);

            //set animation to all view
            //set close fab because fab are closing
            todayFab.setAnimation(fabOpen);
            everydayFab.setAnimation(fabOpen);
            scheduleFab.setAnimation(fabOpen);
            //rotate main fab
            mainFab.setAnimation(rotateClockwise);

            //set all view to click able
            todayFab.setClickable(true);
            scheduleFab.setClickable(true);
            everydayFab.setClickable(true);

            //now set onClick listener for all view
            //and put extra to intent as task type
            todayFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchAddTaskActivity("Today");
                }
            });

            scheduleFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchAddTaskActivity("Schedule");
                }
            });

            everydayFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchAddTaskActivity("Everyday");
                }
            });

            //at last set is open to true
            isFabOpen = true;
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    /*replaced by another option of recycle view click listener
    @Override
    public void onClickListener(String taskName) {
        Toast.makeText(this, "Click: " + String.valueOf(taskName), Toast.LENGTH_SHORT).show();
    } */


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

}
