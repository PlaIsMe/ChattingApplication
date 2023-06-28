package com.chattingapplication.springbootserver.service.interfaces;

import java.util.List;

import com.chattingapplication.springbootserver.model.Message;

public interface MessageService {
    List<Message> getAllMessages(Long chatRoomId);
    Message createMessage(Long chatRoomId, Long userId, Message message) throws Exception;    
}
