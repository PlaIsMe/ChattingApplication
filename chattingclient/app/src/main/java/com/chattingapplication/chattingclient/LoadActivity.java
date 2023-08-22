package com.chattingapplication.chattingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.chattingapplication.chattingclient.AsyncTask.ConnectTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.Response;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.Utils.SocketResponse;
import com.chattingapplication.chattingclient.Utils.Utils;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    public static String IP;
    public static int PORT = 8081;
    public static String apiUrl;
    public static Context currentContext;
    public static Account currentAccount;

    public static SharedPreferences preferencesCheck;
    public static SharedPreferences preferencesAccount;
    private SocketResponse responseFunction;
    public static List<User> listPeople;
    public static List<Long> idList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        preferencesCheck = getSharedPreferences("check", MODE_PRIVATE);
        preferencesAccount = getSharedPreferences("account", MODE_PRIVATE);
        currentContext = this;
//
//        IP = Utils.getIpV4(this, "my_ipv4.json");
//        apiUrl = String.format("http://%s:8080/api/", IP);
        IP = "34.101.218.243";
        apiUrl = String.format("http://%s:8080/api/", "34.128.104.216");
        init();
        progressBar.setVisibility(View.VISIBLE);
        ConnectTask connectTask = new ConnectTask(this);
        connectTask.execute();
    }

    private void init() {
        this.progressBar = findViewById(R.id.processBar);
    }

    public void handleSocketResponse(String jsonResponse) {
        Gson gson = new Gson();
        Response response = gson.fromJson(jsonResponse, Response.class);
        try {
            Log.d("debugCurrentContext", currentContext.toString());
            Method responseMethod = SocketResponse.class.getDeclaredMethod(response.getResponseFunction(), String.class);
            responseMethod.invoke(new SocketResponse(currentContext), response.getResponseParam());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}