package com.example.neverendingservicedanielshijakovski.restarter;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import com.example.neverendingservicedanielshijakovski.Globals;
import com.example.neverendingservicedanielshijakovski.Service;

public class RestartServiceBroadcastReceiver extends BroadcastReceiver {
    private static JobScheduler jobScheduler;

    @Override
    public void onReceive(final Context context, Intent intent) { scheduleJob(context); }

    public static void scheduleJob(Context context) {
        if (jobScheduler == null) {
            jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        }

        JobInfo jobInfo = new JobInfo
                .Builder(1, new ComponentName(context, JobService.class))
                .setOverrideDeadline(0) // Run immediately
                .setPersisted(true) // Restart the job when the phone is rebooted
                .build();
        jobScheduler.schedule(jobInfo);
    }

}
