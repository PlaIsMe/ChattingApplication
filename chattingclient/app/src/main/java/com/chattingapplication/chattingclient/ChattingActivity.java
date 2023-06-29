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
import com.google.gson.Gson;

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

        LoadActivity.chattingContext = this;

        FragmentManager fragmentManager = getSupportFragmentManager();
        chattingFragment = fragmentManager.findFragmentById(R.id.fragmentContainerChatting);

        Intent prevIntent = getIntent();
        Log.d("debugIntent", prevIntent.getStringExtra("targetUser"));
        Gson gson = new Gson();
        targetUser = gson.fromJson(prevIntent.getStringExtra("targetUser"), User.class);

        GetRequestTask getRequestTask = new GetRequestTask(this);
        String path = String.format("chat_room/%s/%s/true", AuthenticationActivity.currentAccount.getUser().getId(), targetUser.getId());
        getRequestTask.execute(path, "joinPrivateRoom", "ChattingFragment", "ChattingActivity");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.default_avatar);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle(String.format("%s %s", targetUser.getFirstName(), targetUser.getLastName()));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}