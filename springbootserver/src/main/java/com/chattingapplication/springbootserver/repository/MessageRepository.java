package com.chattingapplication.springbootserver.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chattingapplication.springbootserver.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("SELECT m FROM MessageEntity m JOIN m.chatRoom c WHERE c.id = ?1")
    Set<MessageEntity> findByChatRoomId(Long chatRoomId);

}
