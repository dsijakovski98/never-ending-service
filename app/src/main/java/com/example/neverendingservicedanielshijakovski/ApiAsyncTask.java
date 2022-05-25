package com.example.neverendingservicedanielshijakovski;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;

    public ApiAsyncTask(Context context) {
        super();
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(Globals.LOG_TAG, "doInBackground()");

        String resultString = NetworkUtils.getInfo();

        if (resultString == null) {
            Log.i(Globals.LOG_TAG, "Something went wrong when fetching result");
            return null;
        }

        try {
            // Parse the json string into an object
            JSONArray jobs = new JSONArray(resultString);
            JSONObject job = jobs.getJSONObject(0);

            JobExecutor.handleJob(job, context.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}