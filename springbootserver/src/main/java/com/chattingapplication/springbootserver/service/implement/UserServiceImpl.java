package com.chattingapplication.springbootserver.service.implement;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.chattingapplication.springbootserver.entity.UserEntity;
import com.chattingapplication.springbootserver.model.User;
import com.chattingapplication.springbootserver.repository.UserRepository;
import com.chattingapplication.springbootserver.service.interfaces.ChatRoomService;
import com.chattingapplication.springbootserver.service.interfaces.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    
    @Override
    public List<User> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities
            .stream().map(user -> new User(
                user.getId(),
                user.getLastName(),
                user.getFirstName(),
                user.getAvatar(),
                user.getGender(),
                user.getDob(),
                chatRoomService.getChatRoomsByUserId(user.getId())
            )).collect(Collectors.toList());
    }

    @Override
    public User createUser(User user) throws Exception {
        try {
            UserEntity userEntity = new UserEntity();
            user.setId(userRepository.save(userEntity).getId());
            return user;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean deleteUser(Long userId) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(userId).get();
            userRepository.delete(userEntity);
            return true;
        } catch (NoSuchElementException e) {
            throw new Exception("User not found!");
        }
    }

    @Override
    public User updateUser(Long userId, User user) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(userId).get();
            userEntity.setDob(user.getDob());
            userEntity.setFirstName(user.getFirstName());
            userEntity.setGender(user.getGender());
            userEntity.setLastName(user.getLastName());
            userRepository.save(userEntity);
            user.setId(userId);

            return user;
        } catch (NoSuchElementException e) {
            throw new Exception("User not found!");
        }
    }

    @Override
    public User getUserById(Long userId) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(userId).get();
            User user = new User();
            BeanUtils.copyProperties(userEntity, user);
            user.setChatRooms(chatRoomService.getChatRoomsByUserId(user.getId()));
            return user;
        } catch (NoSuchElementException e) {
            throw new Exception("User not found!");
        }
    }
}
