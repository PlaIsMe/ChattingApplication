package com.chattingapplication.springbootserver.service.implement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.chattingapplication.springbootserver.entity.ChatRoomEntity;
import com.chattingapplication.springbootserver.entity.MessageEntity;
import com.chattingapplication.springbootserver.entity.UserEntity;
import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.model.Message;
import com.chattingapplication.springbootserver.model.User;
import com.chattingapplication.springbootserver.repository.ChatRoomRepository;
import com.chattingapplication.springbootserver.repository.MessageRepository;
import com.chattingapplication.springbootserver.repository.UserRepository;
import com.chattingapplication.springbootserver.service.interfaces.MessageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    public List<Message> getAllMessages(Long chatRoomId) {
        Set<MessageEntity> messageEntities = messageRepository.findByChatRoomId(chatRoomId);
        System.out.println(messageEntities);
        return messageEntities
            .stream().map(message -> new Message(
                message.getId(),
                message.getContent(),
                message.getCreateAt(),
                new User(
                    message.getUser().getId(),
                    message.getUser().getFirstName(),
                    message.getUser().getLastName(),
                    message.getUser().getGender(),
                    message.getUser().getDob()
                )
            )).collect(Collectors.toList());
    }

    @Override
    public Message createMessage(Long chatRoomId, Long userId, Message message) throws Exception {
        Optional<ChatRoomEntity> chatRoomEntityOptional = chatRoomRepository.findById(chatRoomId);
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (!chatRoomEntityOptional.isPresent()) {
            throw new Exception("Invalid chat room id");
        } else if (!userEntityOptional.isPresent()) {
            throw new Exception("Invalid user id");
        } else {
            ChatRoomEntity chatRoomEntity = chatRoomEntityOptional.get();
            UserEntity userEntity = userEntityOptional.get();
            if (userEntity.getChatRooms().contains(chatRoomEntity)) {
                MessageEntity messageEntity = new MessageEntity();
                message.setCreateAt(LocalDateTime.now());
                BeanUtils.copyProperties(message, messageEntity);
                // chatRoomEntity.getMessages().add(messageEntity);
                messageEntity.setChatRoom(chatRoomEntity);
                messageEntity.setUser(userEntity);
                chatRoomRepository.save(chatRoomEntity);
                message.setId(messageRepository.save(messageEntity).getId());
                User returnUser = new User();
                ChatRoom returnChatRoom = new ChatRoom();
                BeanUtils.copyProperties(userEntity, returnUser);
                BeanUtils.copyProperties(chatRoomEntity, returnChatRoom);
                message.setChatRoom(returnChatRoom);
                message.setUser(returnUser);
                return message;
            } else {
                throw new Exception("User not in chat room");
            }
        }
    }
}
