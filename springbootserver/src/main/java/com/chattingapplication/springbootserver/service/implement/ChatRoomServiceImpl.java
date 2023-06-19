package com.chattingapplication.springbootserver.service.implement;

import java.util.List;

import com.chattingapplication.springbootserver.entity.ChatRoomEntity;
import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.model.User;
import com.chattingapplication.springbootserver.service.interfaces.ChatRoomService;

public class ChatRoomServiceImpl implements ChatRoomService {

    @Override
    public List<ChatRoom> getChatRoomByUser(User user) {
        // List<ChatRoomEntity>
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChatRooms'");
    }

    @Override
    public List<ChatRoom> getChatRooms() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChatRooms'");
    }

    @Override
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createChatRoom'");
    }

    @Override
    public void deleteChatRoom(ChatRoom chatRoom) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteChatRoom'");
    }
    
}
