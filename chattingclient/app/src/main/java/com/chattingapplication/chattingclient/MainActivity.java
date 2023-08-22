package com.chattingapplication.chattingclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
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
        ImageView view = findViewById(R.id.imageViewChats);
        view.setImageResource(R.drawable.chats);
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
        actionBar.setElevation(0);
//        actionBar.setTitle(String.format("%s %s",
//                LoadActivity.currentAccount.getUser().getFirstName(),
//                LoadActivity.currentAccount.getUser().getLastName()));
        actionBar.setTitle("Chats");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatting_menu, menu);

//        MenuItem menuItem = menu.findItem(R.id.searchBar);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setQueryHint("Type user name");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            LoadActivity.currentAccount = null;
            SharedPreferences.Editor editorCheck = LoadActivity.preferencesCheck.edit();
            editorCheck.putString("check", "false");
            editorCheck.apply();
            SendTask sendTask = new SendTask();
            sendTask.execute("logOut", "");
            Intent authenticationActivity = new Intent(this, AuthenticationActivity.class);
            startActivity(authenticationActivity);
            this.finish();
        }
//        else if (item.getItemId() == R.id.searchBar) {
//            Intent searchIntent = new Intent(this, SearchActivity.class);
//            startActivity(searchIntent);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}