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

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobService extends android.app.job.JobService {
    private static final String TAG= JobService.class.getSimpleName();
    private static RestartServiceBroadcastReceiver restartSensorServiceReceiver;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ProcessMainClass bck = new ProcessMainClass();
        bck.launchService(this);
        registerRestarterReceiver();

        return false;
    }

    private void registerRestarterReceiver() {

        if (restartSensorServiceReceiver == null)
            restartSensorServiceReceiver = new RestartServiceBroadcastReceiver();
        else try{
            unregisterReceiver(restartSensorServiceReceiver);
        } catch (Exception e){
            // not registered
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
                    // handle error here
                }
            }
        }, 1000);

    }

    /**
     * called if Android kills the job service
     * @param jobParameters parameters for the job
     * @return boolean
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Stopping job");
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // give the time to run
        new Handler().postDelayed(() -> unregisterReceiver(restartSensorServiceReceiver), 1000);

        return false;
    }
}