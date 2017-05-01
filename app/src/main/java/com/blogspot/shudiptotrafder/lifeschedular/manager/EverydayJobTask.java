package com.blogspot.shudiptotrafder.lifeschedular.manager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

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

        //Intent serviceIntent = new Intent(this,DueTaskService.class);
        //startService(serviceIntent);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
