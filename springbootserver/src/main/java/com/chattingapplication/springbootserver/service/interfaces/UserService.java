package com.chattingapplication.springbootserver.service.interfaces;

import java.util.List;

import com.chattingapplication.springbootserver.model.User;

public interface UserService {
  List<User> getAllUsers();

  User createUser(User user) throws Exception;

  boolean deleteUser(Long userId) throws Exception;

  User updateUser(Long userId, User user) throws Exception;

  User getUserById(Long userId) throws Exception;
}
