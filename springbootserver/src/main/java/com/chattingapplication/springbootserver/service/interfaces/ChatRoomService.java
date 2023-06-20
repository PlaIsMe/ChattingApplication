package com.chattingapplication.springbootserver.service.interfaces;

import java.util.List;

import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.model.User;

public interface ChatRoomService {
    List<ChatRoom> getChatRooms();
    ChatRoom createChatRoom(ChatRoom chatRoom) throws Exception;
    void deleteChatRoom(ChatRoom chatRoom) throws Exception;
    void addUsers(ChatRoom chatRoom, List<User> users) throws Exception;
}
