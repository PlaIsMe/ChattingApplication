package com.chattingapplication.springbootserver.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "last_name", nullable = true, length = 40)
    private String lastName;

    @Column(name = "first_name", nullable = true, length = 40)
    private String firstName;

    @Column(name = "avatar", nullable = true, length = 300)
    private String avatar;

    @Column(name = "gender", nullable = true, length = 20)
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dob", nullable = true)
    private LocalDateTime dob;

    @ManyToMany
    @JoinTable(
        name = "user_chat_room",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "chat_room_id")
    )
    private Set<ChatRoomEntity> chatRooms;

    // @OneToOne(mappedBy = "user")
    // private AccountEntity account;
}