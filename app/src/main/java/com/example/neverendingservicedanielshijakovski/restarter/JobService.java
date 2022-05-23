package com.example.neverendingservicedanielshijakovski.restarter;

import android.app.job.JobParameters;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.neverendingservicedanielshijakovski.Globals;
import com.example.neverendingservicedanielshijakovski.ProcessMainClass;

public class JobService extends android.app.job.JobService {
    private static RestartServiceBroadcastReceiver restartSensorServiceReceiver;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ProcessMainClass bck = new ProcessMainClass();
        bck.launchService(this);
        registerRestarterReceiver();
        return false;
    }

    private void registerRestarterReceiver() {

        try {
            if (restartSensorServiceReceiver == null) {
                restartSensorServiceReceiver = new RestartServiceBroadcastReceiver();
            } else {
                unregisterReceiver(restartSensorServiceReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // give the time to run
        new Handler().postDelayed(() -> {
            // we register the  receiver that will restart the background service if it is killed
            // see onDestroy of Service
            IntentFilter filter = new IntentFilter();
            filter.addAction(Globals.RESTART_INTENT);
            try {
                registerReceiver(restartSensorServiceReceiver, filter);
            } catch (Exception e) {
                try {
                    getApplicationContext().registerReceiver(restartSensorServiceReceiver, filter);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 1000);

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);

        // give the time to run
        new Handler().postDelayed(() -> unregisterReceiver(restartSensorServiceReceiver), 1000);

        return false;
    }
}