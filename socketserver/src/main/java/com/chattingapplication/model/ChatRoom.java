package com.chattingapplication.model;

import lombok.Data;

@Data
public class ChatRoom {
    private Long id;
    private String roomName;
    private boolean isPrivate;
    private Message latestMessage;
    private User targetUser;

    @Override
    public boolean equals(Object object) {
        if (object instanceof ChatRoom){
            ChatRoom chatRoom = (ChatRoom) object;
            return chatRoom.id.longValue() == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
