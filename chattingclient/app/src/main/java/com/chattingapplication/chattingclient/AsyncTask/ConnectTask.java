package com.chattingapplication.chattingclient.AsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.chattingapplication.chattingclient.AuthenticationActivity;
import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.R;
import com.chattingapplication.chattingclient.Utils.Utils;
import com.google.gson.Gson;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (clientFd.isConnected()) {
                    try {
                        socketResponse = dIn.readUTF();
                        ((LoadActivity) activity).handleSocketResponse(socketResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        String loginSuccess = LoadActivity.preferencesCheck.getString("check", "false");
        if (loginSuccess.equals("true")) {
            Gson gson = new Gson();
            LoadActivity.currentAccount = gson.fromJson(LoadActivity.preferencesAccount.getString("account", ""), Account.class);
            try {
                String checkName = LoadActivity.currentAccount.getUser().getFirstName();
                Log.d("debugCheckName", checkName);
                SendTask sendTask = new SendTask();
                sendTask.execute("updateRequest", String.format("%s", gson.toJson(LoadActivity.currentAccount)));
            } catch (NullPointerException e) {
                Intent authenticationActivity = new Intent(this.activity, AuthenticationActivity.class);
                authenticationActivity.putExtra("navigate", "true");
                activity.startActivity(authenticationActivity);
            }
        } else {
            Intent authenticationActivity = new Intent(this.activity, AuthenticationActivity.class);
            activity.startActivity(authenticationActivity);
        }
    }
}
