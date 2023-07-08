package com.chattingapplication.chattingclient.Model;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class User {
    private Long id;
    private String lastName;
    private String firstName;
    private Date dob;
    private String avatar;
    private File uploadAvatar;
    private String gender;
    private List<ChatRoom> chatRooms;

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public User() {

    }

    public User(Long id, String lastName, String firstName) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", dob=" + dob +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", chatRooms=" + chatRooms +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return Objects.equals(((User) obj).getId(), id);
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public File getUploadAvatar() {
        return uploadAvatar;
    }

    public void setUploadAvatar(File uploadAvatar) {
        this.uploadAvatar = uploadAvatar;
    }
}
