package com.chattingapplication.springbootserver.service.interfaces;

import java.util.List;
import java.util.Set;

import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.model.Message;
import com.chattingapplication.springbootserver.model.User;

public interface ChatRoomService {
    List<ChatRoom> getChatRooms();
    ChatRoom createChatRoom(ChatRoom chatRoom) throws Exception;
    void deleteChatRoom(Long chatRoomId) throws Exception;
    String addUsers(Long chatRoomId, List<User> users) throws Exception;
    User addUser(Long chatRoomId, User user) throws Exception;
    Set<ChatRoom> getChatRoomsByUserId(Long userId);
    ChatRoom getChatRoomByUsers(Long currentUserId, Long targetUserId, boolean isPrivate) throws Exception;
    ChatRoom createPrivateRoom(Long user_id_created, Long user_id_targed, Message message) throws Exception;
}
