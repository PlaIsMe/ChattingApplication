package com.chattingapplication.chattingclient.Utils;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.ChattingFragment;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.R;
import com.google.gson.Gson;

import Service.NotificationService;

public class ResponseFunction {
    private Context context;

    public ResponseFunction(Context context) {
        this.context = context;
    }

//    ChattingContext --------------------------------------------------------------------------------------------------------------------
    public void chattingResponse(String message) {
        ((ChattingActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NotificationService.sendNotification(context, message);
                ((ChattingFragment) ((ChattingActivity) context).getChattingFragment()).appendOtherMsg(message);
            }
        });
    }

    public void createPrivateRoomResponse(String chatRoom) {
        if (context instanceof ChattingActivity) {
            Gson gson = new Gson();
            ChatRoom room = gson.fromJson(chatRoom, ChatRoom.class);
            ((ChattingActivity) context).setCurrentChatRoom(room);
            ((ChattingActivity) context).setRoomAvailable(true);
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
}
