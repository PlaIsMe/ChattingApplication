package com.chattingapplication.springbootserver.service.interfaces;

import java.util.List;

import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.model.User;

public interface ChatRoomService {
    List<ChatRoom> getChatRoomByUser(User user);
    List<ChatRoom> getChatRooms();
    ChatRoom createChatRoom(ChatRoom chatRoom);
    void deleteChatRoom(ChatRoom chatRoom);
}
