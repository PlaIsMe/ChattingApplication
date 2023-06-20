package com.chattingapplication.springbootserver.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.service.interfaces.ChatRoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/chat_room")
@RequiredArgsConstructor
public class ChatRoomController  {
    private final ChatRoomService chatRoomService;

    @PostMapping("/create_chat_room")
    public ChatRoom createChatRoom(@Valid @RequestBody ChatRoom chatRoom,
        BindingResult result) throws Exception{
        if(result.hasErrors()){
            throw new Exception(result.getAllErrors().get(0).getDefaultMessage());
        }

        return chatRoomService.createChatRoom(chatRoom);
    }
}
