package com.chattingapplication.springbootserver.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@Table(name = "chat_room")
public class ChatRoomEntity implements Serializable {
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

    // @OneToMany(mappedBy = "chatRoom")
    // private List<MessageEntity> messages;
}