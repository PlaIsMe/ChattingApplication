package com.chattingapplication.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Message {
    private Long id;
    private String content;
    // private LocalDateTime createAt;
    private User user;
    private ChatRoom chatRoom;

    public Message(User user, String content) {
        this.user = user;
        this.content = content;
    }
}
