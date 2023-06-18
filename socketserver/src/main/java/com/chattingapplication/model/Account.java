package com.chattingapplication.model;

public class Account {
    private Long id;
    private String email;
    private String password;

    @Override
    public String toString() {
        return "Account [id=" + id + ", email=" + email + ", password=" + password + "]";
    }

    public Account() {
        
    }
    
    public Account(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
