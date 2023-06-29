package com.chattingapplication.chattingclient.AsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.chattingapplication.chattingclient.AuthenticationActivity;
import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectTask extends AsyncTask<Void, Void, Void> {
    public static Socket clientFd;
    public static DataOutputStream dOut;
    public static DataInputStream dIn;
    private String socketResponse;
    private Activity activity;

    public ConnectTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            clientFd = new Socket(LoadActivity.IP, LoadActivity.PORT);
            dOut = new DataOutputStream(clientFd.getOutputStream());
            dIn = new DataInputStream(clientFd.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        Intent authenticationActivity = new Intent(this.activity, AuthenticationActivity.class);
        activity.startActivity(authenticationActivity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (clientFd.isConnected()) {
                    try {
                        // Luồng luôn đọc từ client
                        socketResponse = dIn.readUTF();
                        Log.d("debugReceived", socketResponse);
                        ((LoadActivity) activity).handleSocketResponse(socketResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
