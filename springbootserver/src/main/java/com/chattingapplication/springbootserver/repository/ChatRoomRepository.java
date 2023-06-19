package com.chattingapplication.springbootserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chattingapplication.springbootserver.entity.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long>{
    
}
