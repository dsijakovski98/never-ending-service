package com.example.neverendingservicedanielshijakovski;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new ApiAsyncTask().execute();
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
