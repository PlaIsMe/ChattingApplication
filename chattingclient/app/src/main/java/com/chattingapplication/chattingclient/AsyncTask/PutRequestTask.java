package com.chattingapplication.chattingclient.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.chattingapplication.chattingclient.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class PutRequestTask extends AsyncTask<String, String, Void> {
    private MainActivity mainActivity;

    public PutRequestTask(MainActivity activity) {
        mainActivity = activity;
    }
    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(MainActivity.apiUrl + params[0]);
            Log.d("debugPutURL", MainActivity.apiUrl + params[0]);
            Log.d("debugPutContent", params[1]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(params[1]);
            osw.flush();
            osw.close();

            BufferedReader in;
            Log.d("debugResponseCode", String.valueOf(conn.getResponseCode()));
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                mainActivity.readResponseBody(in);
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                mainActivity.readResponseBody(in);
            }

            Class<?> c = mainActivity.getClass();
            Method method = c.getDeclaredMethod(params[2], int.class);
            method.invoke(mainActivity, conn.getResponseCode());

        } catch (IOException e) {
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}