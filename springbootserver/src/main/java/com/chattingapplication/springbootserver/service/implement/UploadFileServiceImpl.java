package com.chattingapplication.springbootserver.service.implement;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.chattingapplication.springbootserver.entity.UserEntity;
import com.chattingapplication.springbootserver.model.User;
import com.chattingapplication.springbootserver.repository.UserRepository;
import com.chattingapplication.springbootserver.service.interfaces.UploadFileService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class UploadFileServiceImpl implements UploadFileService {
    @Autowired
    private Cloudinary serverCloudinary;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<String> uploadAvatar(User user) throws IOException {
        try {
            Optional<UserEntity> userEntityOptional = userRepository.findById(user.getId());
            System.out.println("OVER HEREREEEEEEEEEEEEEEEEEEE: " + userEntityOptional.get());
            if (userEntityOptional.isPresent()) {
                String secure_url = serverCloudinary.uploader()
                        .upload(user.getUploadAvatar().getBytes(),
                                ObjectUtils.asMap("resource_type", "auto"))
                        .get("secure_url").toString();
                // user.setAvatar(secure_url);
                UserEntity userEntity = userEntityOptional.get();
                userEntity.setAvatar(secure_url);
                userRepository.save(userEntity);
            }

            return ResponseEntity.ok().body("Upload avatar sucessful");
        } catch (IOException e) {
            throw new IOException();
        }
    }

}
