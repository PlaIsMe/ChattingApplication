package com.chattingapplication.model;

import lombok.Data;

@Data
public class ChatRoom {
    private Long id;
    private String roomName;
    private boolean isPrivate;
}
