package com.chattingapplication.model;

import java.sql.Date;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String lastName;
    private String firstName;
    private Date dob;
    private String avatar;
    private String gender;
}
