package com.chattingapplication.chattingclient.Model;

public class ChatRoom {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Message getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(Message latestMessage) {
        this.latestMessage = latestMessage;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    private Long id;
    private String roomName;
    private boolean isPrivate;
    private Message latestMessage;
    private User targetUser;


    public ChatRoom(Long id, String roomName, boolean isPrivate, Message latestMessage, User targetUser) {
        this.id = id;
        this.roomName = roomName;
        this.isPrivate = isPrivate;
        this.latestMessage = latestMessage;
        this.targetUser = targetUser;
    }

    public ChatRoom(Long id, String roomName, boolean isPrivate) {
        this.id = id;
        this.roomName = roomName;
        this.isPrivate = isPrivate;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", isPrivate=" + isPrivate +
                ", latestMessage=" + latestMessage +
                ", targetUser=" + targetUser +
                '}';
    }
}
