package com.chattingapplication.chattingclient.Model;

public class Message {
    private Long id;
    private String content;
    // private LocalDateTime createAt;
    private User user;


    public Message(Long id, String content, User user) {
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

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", user=" + user +
                '}';
    }
}
