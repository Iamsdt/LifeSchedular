package com.blogspot.shudiptotrafder.lifeschedular.services;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.blogspot.shudiptotrafder.lifeschedular.MainActivity;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular.services
 * Created by Shudipto Trafder on 4/24/2017 at 4:10 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class EverydayTaskSynv {

     /*
     * Interval at which to sync with the task data. Use TimeUnit for convenience, rather than
     * writing out a bunch of multiplication ourselves and risk making a silly mistake.
     */

    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String EVERYDAY_TASK_SYNC_TAG = "everyday_sync";

    /**
     * Schedules a repeating sync of everyday type task data using FirebaseJobDispatcher.
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher
     */
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync everyday task */
        Job syncSunshineJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Sunshine's data */
                .setService(EverydayTaskServices.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(EVERYDAY_TASK_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                //.setConstraints(Constraint.ON_ANY_NETWORK)
                //we don't need any network connection
                //but learning purpose we set it And comment out here
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want every day type task data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the everyday task type data to be synced every 24 hours(on a day). The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncSunshineJob);
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    synchronized public static void initialize(@NonNull final Context context) {

        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;

        sInitialized = true;

        /*
         * This method call triggers Sunshine to create its task to synchronize weather data
         * periodically.
         */
        scheduleFirebaseJobDispatcherSync(context);

        /*
         * We need to check to see if our ContentProvider has data to display in our forecast
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {


                String selection = DB_Contract.Entry.COLUMN_TASK_TYPE +" =?"
                        +" AND "+ DB_Contract.Entry.COLUMN_TASK_STATUS +" =?";

                String[] selectionArg = new String[]{MainActivity.EVERYDAY,"1"};

                //here we check is any every day type task is true
                // if found then our service is not working
                // or some data to left
                Cursor cursor = context.getContentResolver().query(
                        DB_Contract.Entry.CONTENT_URI,
                        null,
                        selection,
                        selectionArg,
                        null);

                //if we found some data
                //that's means we need to run our service again

                if (cursor != null){
                    if (cursor.getCount() > 0) {
                        startImmediateSync(context);
                    }
                    cursor.close();
                }

                /* Make sure to close the Cursor to avoid memory leaks! */

            }
        });

        /* Finally, once the thread is prepared, fire it off to perform our checks. */
        checkForEmpty.start();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, EveryDayIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}
