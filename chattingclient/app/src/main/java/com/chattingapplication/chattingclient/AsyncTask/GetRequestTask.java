package com.chattingapplication.chattingclient.AsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Utils.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequestTask extends AsyncTask<String, Void, String> {
    private Activity activity;
    private String functionName;
    private int responseCode;
    private HttpResponse httpResponse;

    public GetRequestTask(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(LoadActivity.apiUrl + params[0]);
            Log.d("debugGetURL", LoadActivity.apiUrl + params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            responseCode = conn.getResponseCode();
            functionName = params[1];

            BufferedReader bufferedReader = (responseCode == HttpURLConnection.HTTP_OK ?
                    new BufferedReader(new InputStreamReader(conn.getInputStream())) :
                    new BufferedReader(new InputStreamReader(conn.getErrorStream())));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();
            return response.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            Log.d("debugGetResponse", s);
            Method responseMethod = HttpResponse.class.getDeclaredMethod(functionName, int.class, String.class);
            responseMethod.invoke(httpResponse, responseCode, s);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
