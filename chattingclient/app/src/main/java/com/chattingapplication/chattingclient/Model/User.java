package com.chattingapplication.chattingclient.Model;

import java.util.Date;

public class User {
    private Long id;
    private String lastName;
    private String firstName;
    private Date dob;
    private String avatar;
    private String gender;

    public User() {

    }

    public User(Long id, String lastName, String firstName, Date dob, String avatar, String gender) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.dob = dob;
        this.avatar = avatar;
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
