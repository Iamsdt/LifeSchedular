package com.blogspot.shudiptotrafder.lifeschedular.manager;

import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

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

public class EverydayJobTask extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {

        //job scheduler do this job every day at 23.59.59
        //every day task sync is an async task
        EverydayTaskSync taskSync = new EverydayTaskSync(this);
        taskSync.execute();

        //it's is an intent service to due task
        Intent serviceIntent = new Intent(this, DueTaskService.class);
        startService(serviceIntent);

        //this is only testing purpose only
        //if job schedule then set a notification
        TaskNotification.notify(this, "Starting Task sync: ", 0);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
