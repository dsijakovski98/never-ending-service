package com.example.neverendingservicedanielshijakovski;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String BASE_URL =  "http://10.211.55.3:5000/getjobs/emulator";

    // Algorithm for parsing the response we get from an API call
    private static String parseInput(HttpURLConnection connection) {
        InputStream inputStream;
        BufferedReader reader = null;

        try {
            inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if (builder.length() == 0) {
                return null;
            }

            return builder.toString();

        } catch (IOException e) {
            // Handle readers errors
            e.printStackTrace();
            return null;
        } finally {
            // Close connection and reader
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static String getInfo() {
        Log.d(Globals.LOG_TAG, "getInfo()");
        HttpURLConnection urlConnection = null;
        String JSONString;

        try {
            Uri builtURI = Uri.parse(BASE_URL).buildUpon().build();
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            JSONString = parseInput(urlConnection);

        } catch (IOException e) {
            Log.i(Globals.LOG_TAG, "Nesho bidna");
            Log.i(Globals.LOG_TAG, e.toString());
            e.printStackTrace();
            return null;
        }

        return JSONString;
    }
}
