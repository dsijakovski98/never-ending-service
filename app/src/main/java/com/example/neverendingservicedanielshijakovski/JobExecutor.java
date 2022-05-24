package com.example.neverendingservicedanielshijakovski;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// Executes a different command depending on the job that gets passed into it
public class JobExecutor {
    public static Void handleJob(JSONObject job) {
        try {
            String jobType = job.getString("jobType");
            switch (jobType) {
                case "PING":
                    pingJob(job);
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

    private static Void pingJob(JSONObject job) {
        BufferedReader in = null;
        try {
            // Ping command
            // -i flag => time interval
            int interval = 2; // job.getInt("jobPeriod");

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

            // Post info to BE
            NetworkUtils.postInfo(bodyString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
