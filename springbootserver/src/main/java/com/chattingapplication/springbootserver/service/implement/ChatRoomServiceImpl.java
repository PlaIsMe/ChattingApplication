package com.chattingapplication.springbootserver.service.implement;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.chattingapplication.springbootserver.entity.ChatRoomEntity;
import com.chattingapplication.springbootserver.entity.UserEntity;
import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.model.User;
import com.chattingapplication.springbootserver.repository.ChatRoomRepository;
import com.chattingapplication.springbootserver.repository.UserRepository;
import com.chattingapplication.springbootserver.service.interfaces.ChatRoomService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAll().stream().map(r ->{
            ChatRoom room = new ChatRoom();
            room.setId(r.getId());
            room.setPrivate(r.isPrivate());
            room.setRoomName(r.getRoomName());
            
            return room;
        }).collect(Collectors.toList());
    }

    // Create a chat room with empty users
    @Override
    public ChatRoom createChatRoom(ChatRoom chatRoom) throws Exception {
        try {
            ChatRoomEntity chatRoomEntity = new ChatRoomEntity();
            BeanUtils.copyProperties(chatRoom, chatRoomEntity);
            chatRoomRepository.save(chatRoomEntity);
            chatRoom.setId(chatRoomEntity.getId());
            return chatRoom;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void deleteChatRoom(Long chatRoomId) throws Exception {
        try {
            ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatRoomId).get();
            chatRoomEntity.getUsers().stream().forEach(u -> {
                u.getChatRooms().remove(chatRoomEntity);
                userRepository.save(u);
            });
            chatRoomEntity.getUsers().clear();
            chatRoomRepository.save(chatRoomEntity);
            chatRoomRepository.delete(chatRoomEntity);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void addUsers(Long chatRoomId, List<User> users) throws Exception {
        try {
            ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatRoomId).get();
            Set<UserEntity> userEntities = users.stream().map(u ->{
                return userRepository.findById(u.getId()).get();
            }).collect(Collectors.toSet());

            // adding user entities to a chat room
            chatRoomEntity.setUsers(userEntities);

            // adding chat room to each user 
            userEntities.stream().forEach(us -> {
                us.getChatRooms().add(chatRoomEntity);
            });

            chatRoomRepository.save(chatRoomEntity);
            userRepository.saveAll(userEntities);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        
    }

    @Override
    public User addUser(Long chatRoomId, User user) throws Exception {
        try {
            ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatRoomId).get();
            UserEntity userEntity = userRepository.findById(user.getId()).get();
            chatRoomEntity.getUsers().add(userEntity);
            userEntity.getChatRooms().add(chatRoomEntity);
            chatRoomRepository.save(chatRoomEntity);
            userRepository.save(userEntity);
            return user;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
}
