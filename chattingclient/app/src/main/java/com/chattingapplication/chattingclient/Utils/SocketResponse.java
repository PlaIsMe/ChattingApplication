package com.chattingapplication.chattingclient.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chattingapplication.chattingclient.ChatRoomFragment;
import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.ChattingFragment;
import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.Message;
import com.google.gson.Gson;

import com.chattingapplication.chattingclient.Service.NotificationService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SocketResponse {
    private Context context;

    public SocketResponse(Context context) {
        this.context = context;
    }

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
                        ((ChattingActivity) context).updateUiChatRoom(receivedMessage);
                    }
                });
            } else {
//                Ở giao diện chat khác
                NotificationService.sendNotification(context, receivedMessage);
                ((ChattingActivity) context).updateUiChatRoom(receivedMessage);
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
        ChatRoomFragment.chatRoomList.add(0, room);
        LoadActivity.currentAccount.getUser().setChatRooms(ChatRoomFragment.chatRoomList);

        if (context instanceof ChattingActivity) {
            ((ChattingActivity) context).setCurrentChatRoom(room);
            ((ChattingActivity) context).setRoomAvailable(true);
        } else if (context instanceof MainActivity) {
            ((MainActivity) MainActivity.mainContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ChatRoomFragment.chatRoomAdapter.notifyDataSetChanged();
                }
            });
        }

        Message pushMessage = room.getLatestMessage();
        if (!pushMessage.getUser().getId().equals(LoadActivity.currentAccount.getUser().getId())) {
            pushMessage.setChatRoom(new ChatRoom(room.getId(), room.getRoomName(), room.isPrivate()));
            NotificationService.sendNotification(context, pushMessage);
        }
    }

    public void updateResponse(String account) {
        Gson gson = new Gson();
        LoadActivity.currentAccount = gson.fromJson(account, Account.class);
        Log.d("debugAcc", LoadActivity.currentAccount.toString());
    }

    public void onlineUsers(String userIdList) {
        String trimmedString = userIdList.replaceAll("\\[|\\]|\\s", "");
        LoadActivity.idList = Arrays.stream(trimmedString.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        Log.d("debugUserOnl", userIdList);
        Intent mainActivity = new Intent(context, MainActivity.class);
        context.startActivity(mainActivity);
    }
}
