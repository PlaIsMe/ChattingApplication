package com.chattingapplication.model;

import lombok.Data;

@Data
public class Account {
    private Long id;
    private String email;
    private String password;
    private User user;
}
