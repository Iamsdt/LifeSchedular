package com.blogspot.shudiptotrafder.lifeschedular.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.blogspot.shudiptotrafder.lifeschedular.BuildConfig;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class TaskManager extends BroadcastReceiver {

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     * @param t       throwable that's show on log
     */

    private static void slet(String message, Throwable t) {

        final String TAG = "TaskManager";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, t);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase("com.blogspot.shudiptotrafder.lifeschedular")) {
            try {

                int starting = 60 * 60 * 24;
                int finishTime = starting + 600;

                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(
                        new GooglePlayDriver(context));

                dispatcher.mustSchedule(
                        dispatcher.newJobBuilder()
                                .setService(EverydayJobTask.class)
                                .setTag("Everyday Task")
                                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                                .setRecurring(true)
                                .setReplaceCurrent(true)
                                .setTrigger(Trigger
                                        .executionWindow(starting, finishTime))
                                .build());

            } catch (Exception e) {
                slet("Error on async task", e);
            }
        }
    }
}
