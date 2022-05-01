package com.example.neverendingservicedanielshijakovski;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.example.neverendingservicedanielshijakovski.restarter.RestartServiceBroadcastReceiver;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        finish();
    }
}