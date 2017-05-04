package com.blogspot.shudiptotrafder.lifeschedular.manager;

import android.content.Intent;
import android.content.SharedPreferences;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

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

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular
 * Created by Shudipto Trafder on 4/30/2017 at 11:06 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class EverydayJobTask extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {

        EverydayTaskSync taskSync = new EverydayTaskSync(this);
        taskSync.execute();

        Intent serviceIntent = new Intent(this, DueTaskService.class);
        startService(serviceIntent);

        SharedPreferences preferences = getSharedPreferences("Service", MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        Calendar calendar = Calendar.getInstance();

        editor.putString("Service status: ",
                "Start at " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));

        editor.apply();

        TaskNotification.notify(this, "Starting Task sync: ", 0);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
