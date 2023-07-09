package com.chattingapplication.chattingclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattingapplication.chattingclient.AsyncTask.DownloadFromURLTask;
import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.Utils.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.util.List;
import java.util.Optional;

public class ChattingActivity extends AppCompatActivity {
    private User targetUser;
    private ChatRoom currentChatRoom;
    private Fragment chattingFragment;
    private boolean isRoomAvailable = true;
    private int pos;

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
            Log.d("debugIntentCurrentChatRoom", currentChatRoom.toString());
            targetUser = currentChatRoom.getTargetUser();
            Log.d("debugIntentUser", targetUser.toString());
            isRoomAvailable = true;
            GetRequestTask getRequestTask = new GetRequestTask(new HttpResponse(this));
            getRequestTask.execute(String.format("message/%s", currentChatRoom.getId()), "loadMessage");
        } catch (NullPointerException e) {
//            From people fragment
            targetUser = gson.fromJson(prevIntent.getStringExtra("targetUser"), User.class);
            Log.d("debugIntent", targetUser.toString());
            GetRequestTask getRequestTask = new GetRequestTask(new HttpResponse(this));
            String path = String.format("chat_room/%s/%s/true", LoadActivity.currentAccount.getUser().getId(), targetUser.getId());
            getRequestTask.execute(path, "joinPrivateRoom");
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setLogo(R.drawable.default_avatar);
        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setTitle(String.format("%s %s", targetUser.getFirstName(), targetUser.getLastName()));
        View customView = getLayoutInflater().inflate(R.layout.chat_room_menu, null);
        TextView fullName = customView.findViewById(R.id.chatRoomFullName);
        fullName.setText(String.format("%s %s", targetUser.getFirstName(), targetUser.getLastName()));
        ImageView imageView = customView.findViewById(R.id.chatRoomAvatar);
        if(targetUser.getDownloadAvatar() == null){
            new DownloadFromURLTask(targetUser, imageView);
        } else {
            imageView.setImageBitmap(targetUser.getDownloadAvatar());
        }
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customView);
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

    public synchronized void updateUiChatRoom(Message message) {
        List<ChatRoom> chatRoomList = LoadActivity.currentAccount.getUser().getChatRooms();
        ChatRoom currentChatRoom = chatRoomList.stream().filter(c -> c.getId().equals(message.getChatRoom().getId())).findFirst().get();
        LoadActivity.currentAccount.getUser().getChatRooms().remove(currentChatRoom);
        message.setChatRoom(null);
        User messageUser = new User(
                message.getUser().getId(),
                message.getUser().getLastName(),
                message.getUser().getFirstName(),
                message.getUser().getDob(),
                message.getUser().getAvatar(),
                message.getUser().getGender()
        );
        message.setUser(messageUser);
        currentChatRoom.setLatestMessage(message);
        chatRoomList.add(0, currentChatRoom);
        LoadActivity.currentAccount.getUser().setChatRooms(chatRoomList);
    }
}