package com.chattingapplication.springbootserver.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chat_room")
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "room_name", nullable = false, length = 40)
    private String roomName;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @ManyToMany(mappedBy = "chatRooms")
    private Set<UserEntity> users;
}
