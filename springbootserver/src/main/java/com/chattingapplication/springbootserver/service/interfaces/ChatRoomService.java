package com.chattingapplication.springbootserver.service.interfaces;

import java.util.List;

import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.model.User;

public interface ChatRoomService {
    List<ChatRoom> getChatRooms();
    ChatRoom createChatRoom(ChatRoom chatRoom) throws Exception;
    void deleteChatRoom(Long chatRoomId) throws Exception;
    void addUsers(Long chatRoomId, List<User> users) throws Exception;
    User addUser(Long chatRoomId, User user) throws Exception;
}
