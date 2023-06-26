package com.chattingapplication.chattingclient.Model;

public class ChatRoom {
    public ChatRoom(Long id, String roomName, boolean isPrivate) {
        this.id = id;
        this.roomName = roomName;
        this.isPrivate = isPrivate;
    }

    private Long id;
    private String roomName;
    private boolean isPrivate;

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


}
