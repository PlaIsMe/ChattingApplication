package com.chattingapplication.chattingclient.Utils;

import android.content.Context;
import android.util.Log;

import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.ChattingFragment;
import com.chattingapplication.chattingclient.MainActivity;
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
        Log.d("debugFunction", "hello from chatting response");
        Gson gson = new Gson();
        Message receivedMessage = gson.fromJson(message, Message.class);
        if (context instanceof ChattingActivity) {
            if ((((ChattingActivity) context).getTargetUser().getId().equals(receivedMessage.getUser().getId()))) {
                ((ChattingActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ChattingFragment) ((ChattingActivity) context).getChattingFragment()).appendOtherMsg(receivedMessage);
                    }
                });
            } else {
//                Ở giao diện chat khác
                NotificationService.sendNotification(context, receivedMessage);
            }
        } else if (context instanceof MainActivity) {
//            Chưa vào giao diện chat
            NotificationService.sendNotification(context, receivedMessage);
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
