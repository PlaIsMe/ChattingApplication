package com.chattingapplication.springbootserver.service.interfaces;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import com.chattingapplication.springbootserver.model.User;

public interface UploadFileService {
    ResponseEntity<String> uploadAvatar(Long id, User user) throws IOException;
}
