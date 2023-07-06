package com.chattingapplication.chattingclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.Utils.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

public class ChattingActivity extends AppCompatActivity {
    private User targetUser;
    private ChatRoom currentChatRoom;
    private Fragment chattingFragment;
    private boolean isRoomAvailable = true;

    public void setCurrentChatRoom(ChatRoom currentChatRoom) {
        this.currentChatRoom = currentChatRoom;
    }

    public boolean isRoomAvailable() {
        return isRoomAvailable;
    }

    public void setRoomAvailable(boolean roomAvailable) {
        isRoomAvailable = roomAvailable;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public ChatRoom getCurrentChatRoom() {
        return currentChatRoom;
    }

    public Fragment getChattingFragment() {
        return chattingFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        LoadActivity.currentContext = this;

        FragmentManager fragmentManager = getSupportFragmentManager();
        chattingFragment = fragmentManager.findFragmentById(R.id.fragmentContainerChatting);

        Intent prevIntent = getIntent();
        Gson gson = new Gson();
        try {
//            From chat room fragment
            currentChatRoom = gson.fromJson(prevIntent.getStringExtra("currentChatRoom"), ChatRoom.class);
            Log.d("debugIntent", prevIntent.getStringExtra("currentChatRoom"));
            targetUser = currentChatRoom.getTargetUser();
            Log.d("debugIntentUser", String.valueOf(targetUser));
            isRoomAvailable = true;
            GetRequestTask getRequestTask = new GetRequestTask(new HttpResponse(this));
            getRequestTask.execute(String.format("message/%s", currentChatRoom.getId()), "loadMessage");
        } catch (NullPointerException e) {
//            From people fragment
            targetUser = gson.fromJson(prevIntent.getStringExtra("targetUser"), User.class);
            Log.d("debugIntent", prevIntent.getStringExtra("targetUser"));
            GetRequestTask getRequestTask = new GetRequestTask(new HttpResponse(this));
            String path = String.format("chat_room/%s/%s/true", LoadActivity.currentAccount.getUser().getId(), targetUser.getId());
            getRequestTask.execute(path, "joinPrivateRoom");
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.default_avatar);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle(String.format("%s %s", targetUser.getFirstName(), targetUser.getLastName()));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            LoadActivity.currentContext = MainActivity.mainContext;
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}