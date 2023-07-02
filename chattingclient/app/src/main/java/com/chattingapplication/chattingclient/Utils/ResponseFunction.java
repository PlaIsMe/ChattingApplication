package com.chattingapplication.chattingclient.Utils;

import android.content.Context;
import android.util.Log;

import com.chattingapplication.chattingclient.AuthenticationActivity;
import com.chattingapplication.chattingclient.ChatRoomFragment;
import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.ChattingFragment;
import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.PeopleFragment;
import com.google.gson.Gson;

import com.chattingapplication.chattingclient.Service.NotificationService;

import java.util.List;

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
                        ((ChattingFragment) ((ChattingActivity) context).getChattingFragment()).appendMessage(receivedMessage);
                    }
                });
            } else {
//                Ở giao diện chat khác
                NotificationService.sendNotification(context, receivedMessage);
            }
        } else if (context instanceof MainActivity) {
//            Chưa vào giao diện chat
            NotificationService.sendNotification(context, receivedMessage);
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ChatRoomFragment) ((MainActivity) context).getChatRoomFragment()).realTimeUiChatRoom(receivedMessage);
                }
            });
        }
    }

    public void createPrivateRoomResponse(String chatRoom) {
        Gson gson = new Gson();
        ChatRoom room = gson.fromJson(chatRoom, ChatRoom.class);
        ((MainActivity) MainActivity.mainContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatRoomFragment.chatRoomList.add(0, room);
                ChatRoomFragment.chatRoomAdapter.notifyDataSetChanged();
            }
        });

        if (context instanceof ChattingActivity) {
            ((ChattingActivity) context).setCurrentChatRoom(room);
            ((ChattingActivity) context).setRoomAvailable(true);
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------
}
