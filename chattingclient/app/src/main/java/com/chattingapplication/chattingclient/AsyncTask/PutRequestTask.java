package com.chattingapplication.chattingclient.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

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

            BufferedReader in = (conn.getResponseCode() == HttpURLConnection.HTTP_OK ?
                    new BufferedReader(new InputStreamReader(conn.getInputStream())) :
                    new BufferedReader(new InputStreamReader(conn.getErrorStream())));
            Log.d("debugResponseCode", String.valueOf(conn.getResponseCode()));
            mainActivity.readResponseBody(in);

            Log.d("debugClassName", String.format("%s.%s", mainActivity.getPackageName(), params[3]));
            Class<?> mainClass = Class.forName(mainActivity.getPackageName() + ".MainActivity");
            Method getMethod = mainClass.getDeclaredMethod(String.format("get%s", params[3]));
            Object getResult = getMethod.invoke(mainActivity);

            Class<?> fragmentClass = Class.forName(String.format("%s.%s", mainActivity.getPackageName(), params[3]));
            Method responseMethod = fragmentClass.getDeclaredMethod(params[2], int.class);
            responseMethod.invoke((Fragment) getResult, conn.getResponseCode());

        } catch (IOException e) {
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}