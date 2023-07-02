package com.chattingapplication.springbootserver.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chattingapplication.springbootserver.entity.ChatRoomEntity;
import com.chattingapplication.springbootserver.entity.UserEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long>{
    @Query("SELECT c FROM ChatRoomEntity c JOIN c.users u ON u.id = ?1 WHERE c.id IN " +
    "(SELECT c.id FROM ChatRoomEntity c JOIN c.users u ON u.id = ?2)" +
    "AND c.isPrivate = ?3")
    ChatRoomEntity findByUserIds(Long currentUserId, Long targetUserId, boolean isPrivate);

    @Query("SELECT u FROM UserEntity u JOIN u.chatRooms c ON c.id = ?1 WHERE u.id <> ?2 ")
    UserEntity findTargetUser(Long chatRoomId, Long userId);
}
