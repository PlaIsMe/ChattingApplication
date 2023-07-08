package com.chattingapplication.chattingclient.Model;

import android.util.Log;

public class Message {
    private Long id;
    private String content;
    // private LocalDateTime createAt;
    private User user;
    private ChatRoom chatRoom;

    public Message(String content, User user, ChatRoom chatRoom) {
        this.content = content;
        this.user = user;
        this.chatRoom = chatRoom;
    }


    public Message(Long id, String content, User user, ChatRoom chatRoom) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.chatRoom = chatRoom;
    }


    public Message(Long id, String content, User user) {
        this.id = id;
        this.content = content;
        this.user = user;
    }

    public Message(String content, User user) {
        this.id = id;
        this.content = content;
        this.user = user;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", user=" + user +
                ", chatRoom=" + chatRoom +
                '}';
    }
}
