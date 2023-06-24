package com.chattingapplication.chattingclient.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.chattingapplication.chattingclient.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            BufferedReader in = (conn.getResponseCode() == HttpURLConnection.HTTP_OK ?
                    new BufferedReader(new InputStreamReader(conn.getInputStream())) :
                    new BufferedReader(new InputStreamReader(conn.getErrorStream())));
            mainActivity.readResponseBody(in);
            Log.d("debugGetResponse", MainActivity.httpResponse);

            Class<?> mainClass = Class.forName(mainActivity.getPackageName() + ".MainActivity");
            Method getMethod = mainClass.getDeclaredMethod(String.format("get%s", params[2]));
            Object getResult = getMethod.invoke(mainActivity);

            Class<?> fragmentClass = Class.forName(String.format("%s.%s", mainActivity.getPackageName(), params[2]));
            Method responseMethod = fragmentClass.getDeclaredMethod(params[1], String.class);
            responseMethod.invoke((Fragment) getResult, MainActivity.httpResponse);
        } catch (IOException e) {
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
