package com.example.neverendingservicedanielshijakovski;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {

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

            JobExecutor.handleJob(job);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}