package com.chattingapplication.springbootserver.controller;

import java.util.List;
import java.util.Set;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chattingapplication.springbootserver.model.Message;
import com.chattingapplication.springbootserver.service.interfaces.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("{room_id}/{user_id}")
    public Message createMessage(@PathVariable(name = "room_id") Long chatRoomId, @PathVariable(name = "user_id") Long userId,
            @Valid @RequestBody Message message, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new Exception(result.getAllErrors().get(0).getDefaultMessage());
        }
        return messageService.createMessage(chatRoomId, userId, message);
    }

    @GetMapping("{room_id}")
	public List<Message> getAllMessages(@PathVariable(name = "room_id") Long chatRoomId) {
        return messageService.getAllMessages(chatRoomId);
	}
}
