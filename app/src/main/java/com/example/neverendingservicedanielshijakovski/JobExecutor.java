package com.example.neverendingservicedanielshijakovski;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// Executes a different command depending on the job that gets passed into it
public class JobExecutor {
    private static final String NETWORK_PREFS = "NetworkSharedPrefs";
    private static final String CACHED_RESULT_KEY = "result";

    public static Void handleJob(JSONObject job, Context ctx) {
        try {
            String jobType = job.getString("jobType");
            switch (jobType) {
                case "PING":
                    pingJob(job, ctx);
                    break;

                // Can add additional jobs here

                default:
                    return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Void pingJob(JSONObject job, Context ctx) {
        BufferedReader in = null;
        try {
            // Ping command
            // -i flag => time interval
            int interval = job.getInt("jobPeriod");

            // -s flag => packet size
            int packetSize = job.getInt("packetSize");

            // -c flag => number of packets/pings
            int numPackets = job.getInt("count");

            // -host
            String host = job.getString("host");

            String pingCommand = "ping -i " + interval + " -s " + packetSize + " -c " + numPackets + " " + host;
            StringBuilder result = new StringBuilder();

            Process pingProcess = Runtime.getRuntime().exec(pingCommand);

            in = new BufferedReader(new InputStreamReader(pingProcess.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }

            Log.i(Globals.LOG_TAG, "\nPing result:");
            String pingResult = result.toString();
            Log.i(Globals.LOG_TAG, pingResult);

            // Create body
            JSONObject body = new JSONObject();
            body.put("result", pingResult);
            String bodyString = body.toString();

            // Check for network connection
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            SharedPreferences sharedPreferences = ctx.getSharedPreferences(NETWORK_PREFS, Context.MODE_PRIVATE);

            if (!isConnected) {
                // Check if there is something in shared prefs
                String cachedResult = sharedPreferences.getString(CACHED_RESULT_KEY, bodyString);

                // Post info to BE
                NetworkUtils.postInfo(cachedResult);
            } else {
                // Add bodyString to shared prefs
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CACHED_RESULT_KEY, bodyString);
                editor.apply();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
