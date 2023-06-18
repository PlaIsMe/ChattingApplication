package com.chattingapplication.springbootserver.controller;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chattingapplication.springbootserver.model.Account;
import com.chattingapplication.springbootserver.model.User;
import com.chattingapplication.springbootserver.service.interfaces.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;    

    @GetMapping
	public List<User> getAllUsers() {
        return userService.getAllUsers();
	}

    @GetMapping(path = "{userId}")
    public User getUserById(@PathVariable Long userId) throws Exception{
        return userService.getUserById(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user, BindingResult result) throws Exception {
        if (result.hasErrors()){
            throw new Exception(result.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.createUser(user);
    }

    @DeleteMapping(path = "{userId}")
    public void deleteAccount(@PathVariable("userId") Long userId) throws Exception {
        userService.deleteUser(userId);
    }

    @PutMapping(path = "{userId}")
    public User updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody User user, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new Exception(result.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.updateUser(userId, user);        
    }
}
