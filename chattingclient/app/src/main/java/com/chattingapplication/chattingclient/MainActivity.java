package com.chattingapplication.chattingclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.Service.NotificationChannels;
import com.chattingapplication.chattingclient.Utils.HttpResponse;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    private Fragment peopleFragment = new PeopleFragment();
    private Fragment chatRoomFragment;
    public static Context mainContext;
    public static List<User> listPeople;

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
        NotificationChannels.createNotificationChannels(this);

        mainContext = this;
        LoadActivity.currentContext = this;

        GetRequestTask getRequestTask = new GetRequestTask(new HttpResponse(this));
        getRequestTask.execute("user", "loadUser");

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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setLogo(R.drawable.default_avatar);
//        Drawable drawable = new BitmapDrawable()
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle(String.format("%s %s", LoadActivity.currentAccount.getUser().getFirstName(), LoadActivity.currentAccount.getUser().getLastName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            LoadActivity.currentAccount = null;
            SharedPreferences.Editor editorCheck = LoadActivity.preferencesCheck.edit();
            editorCheck.putString("check", "false");
            editorCheck.apply();
            Intent authenticationActivity = new Intent(this, AuthenticationActivity.class);
            startActivity(authenticationActivity);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}