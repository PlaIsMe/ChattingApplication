package com.chattingapplication.chattingclient;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chattingapplication.chattingclient.AsyncTask.ConnectTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.Response;
import com.chattingapplication.chattingclient.Utils.Utils;
import com.google.gson.Gson;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    private Fragment peopleFragment = new PeopleFragment();
    private Fragment chatRoomFragment;
    public static Context mainContext;

    public Fragment getPeopleFragment() {
        return peopleFragment;
    }

    public Fragment getChatRoomFragment() {
        return chatRoomFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = this;
        LoadActivity.currentContext = this;

        SendTask sendTask = new SendTask();
        Gson gson = new Gson();
        sendTask.execute("updateRequest", String.format("%s", gson.toJson(AuthenticationActivity.currentAccount)));
        LinearLayout linearLayoutChats = findViewById(R.id.linearLayoutChats);
        LinearLayout linearLayoutPeople = findViewById(R.id.linearLayoutPeople);
        fragmentManager = getSupportFragmentManager();
        chatRoomFragment = fragmentManager.findFragmentById(R.id.fragmentContainerViewMainMenu);
        linearLayoutChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerViewMainMenu, chatRoomFragment)
                        .commit();
            }
        });

        linearLayoutPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerViewMainMenu, peopleFragment)
                        .commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}