package com.chattingapplication.model;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String lastName;
    private String firstName;
    private Date dob;
    private String avatar;
    private String gender;
    private List<ChatRoom> chatRooms;
}
