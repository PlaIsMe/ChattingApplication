package com.chattingapplication.chattingclient.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.chattingapplication.chattingclient.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequestTask extends AsyncTask<String, Void, Void> {
    private MainActivity mainActivity;

    public GetRequestTask(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(MainActivity.apiUrl + params[0]);
            Log.d("debugGetURL", MainActivity.apiUrl + params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                mainActivity.readResponseBody(in);
                Log.d("debugGetResponse", MainActivity.httpResponse);
            } else {
            }
        } catch (IOException e) {
        }
        return null;
    }
}
