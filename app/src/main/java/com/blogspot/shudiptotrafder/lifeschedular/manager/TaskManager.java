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

public class TaskManager extends BroadcastReceiver {

    /**
     * This methods show log error message with throwable
     *
     * @param t       throwable that's show on log
     */

    private static void slet(Throwable t) {

        final String TAG = "TaskManager";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Error on async task", t);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //if receiver receive action then we schedule a job scheduler
        //that's work on every 24 hours latter
        //work 1 hours
        //and life time is UNTIL_NEXT_BOOT
        //

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

                //test notification
                TaskNotification.notify(context, "Broadcast received", 3);

            } catch (Exception e) {
                slet(e);
            }
        }
    }
}
