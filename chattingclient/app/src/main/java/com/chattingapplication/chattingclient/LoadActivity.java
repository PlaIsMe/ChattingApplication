package com.chattingapplication.chattingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.chattingapplication.chattingclient.AsyncTask.ConnectTask;
import com.chattingapplication.chattingclient.Model.Response;
import com.chattingapplication.chattingclient.Utils.ResponseFunction;
import com.chattingapplication.chattingclient.Utils.Utils;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LoadActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    public static String IP;
    public static int PORT = 8081;
    public static String apiUrl;

    public static Context chattingContext;
    private ResponseFunction responseFunction;

    public static Context getChattingContext() {
        return chattingContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        IP = Utils.getIpV4(this, "my_ipv4.json");
        apiUrl = String.format("http://%s:8080/api/", IP);
        init();
        progressBar.setVisibility(View.VISIBLE);
//        init for avoiding null pointer exception
        chattingContext = this;
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
            Method getMethod = LoadActivity.class.getDeclaredMethod(String.format("get%s", response.getResponseContext()));
            Object contextResult = getMethod.invoke(this);

            responseFunction = new ResponseFunction((Context) contextResult);

            Method responseMethod = ResponseFunction.class.getDeclaredMethod(response.getResponseFunction(), String.class);
            responseMethod.invoke(responseFunction, response.getResponseParam());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}