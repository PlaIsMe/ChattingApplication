package com.chattingapplication.chattingclient.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectTask extends AsyncTask<Void, Void, Void> {
    private static String SERVER_IP;
    private static int SERVER_PORT = 8081;
    public static Socket clientFd;
    public static DataOutputStream dOut;
    public static DataInputStream dIn;
    private String socketResponse;
    private MainActivity mainActivity;

    public ConnectTask(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            SERVER_IP = MainActivity.IP;
            clientFd = new Socket(SERVER_IP, SERVER_PORT);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (clientFd.isConnected()) {
                    try {
                        // Luồng luôn đọc từ client
                        socketResponse = dIn.readUTF();
                        Log.d("debugReceived", socketResponse);
                        mainActivity.handleResponse(socketResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
