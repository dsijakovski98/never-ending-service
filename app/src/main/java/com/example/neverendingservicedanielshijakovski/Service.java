package com.example.neverendingservicedanielshijakovski;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;


public class Service extends android.app.Service {
    private int counter = 0;

    private static final int JOB_ID = 0;
    private JobScheduler mScheduler;

    public Service() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        scheduleJob();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        counter = 0;

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        startTimer();

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                startTimer();
            } catch (Exception e) {
                Log.e(Globals.LOG_TAG, "Error in notification " + e.getMessage());
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }


    // Static to avoid multiple timers to be created when the service is called several times
    private static Timer timer;
    private static TimerTask timerTask;

    public void startTimer() {

        // Set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stopTimer();
        timer = new Timer();

        // Schedule the job we wanna do
        timerTask = new TimerTask() {
            @Override
            public void run() {
                scheduleJob();
            }
        };

        // Schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 10 * 60 * 1000); //
    }

    public void stopTimer() {
        // Stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void scheduleJob() {
        Log.i(Globals.LOG_TAG,"scheduleJob()");
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        int selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;

        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                .setMinimumLatency(3000)
                .setRequiredNetworkType(selectedNetworkOption);

        JobInfo myJobInfo = builder.build();
        mScheduler.schedule(myJobInfo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stopTimer();
    }

}
