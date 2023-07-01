package com.chattingapplication.chattingclient.Utils;

import android.content.Context;

import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.ChattingFragment;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.Message;
import com.google.gson.Gson;

import com.chattingapplication.chattingclient.Service.NotificationService;

public class ResponseFunction {
    private Context context;

    public ResponseFunction(Context context) {
        this.context = context;
    }

//    ChattingContext --------------------------------------------------------------------------------------------------------------------
    public void chattingResponse(String message) {
        if (context instanceof ChattingActivity) {
            Gson gson = new Gson();
            Message receivedMessage = gson.fromJson(message, Message.class);
            ((ChattingActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotificationService.sendNotification(context, receivedMessage);
                    ((ChattingFragment) ((ChattingActivity) context).getChattingFragment()).appendOtherMsg(receivedMessage);
                }
            });
        }
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
