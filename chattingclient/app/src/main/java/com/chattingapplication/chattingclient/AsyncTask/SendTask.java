package com.chattingapplication.chattingclient.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SendTask extends AsyncTask<String, String, Void> {
    @Override
    protected Void doInBackground(String... params) {
        String request;
        try {
            request = new JSONObject()
                    .put("requestFunction", params[0])
                    .put("requestParam", params[1])
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            // Gửi message qua client
            Log.d("debugSent", request);
            ConnectTask.dOut.writeUTF(request);
            ConnectTask.dOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
