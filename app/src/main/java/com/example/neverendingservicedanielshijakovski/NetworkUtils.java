package com.example.neverendingservicedanielshijakovski;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetworkUtils {

    private static final String GET_JOBS =  "http://10.211.55.3:5000/getjobs/emulator";
    private static final String POST_PING =  "http://10.211.55.3:5000/postResults";

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
            Uri builtURI = Uri.parse(GET_JOBS).buildUpon().build();
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            JSONString = parseInput(urlConnection);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return JSONString;
    }

    static Void postInfo(String bodyString) {
        Log.i(Globals.LOG_TAG, "postInfo()");
        HttpURLConnection urlConnection = null;

        try {
            Uri builtURI = Uri.parse(POST_PING).buildUpon().build();
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setDoOutput(true); // Enable writing to connection output stream

            // Write to connection
            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = bodyString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = urlConnection.getResponseCode();

            Log.i(Globals.LOG_TAG, "RESPONSE CODE:");
            Log.i(Globals.LOG_TAG, String.valueOf(responseCode));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
