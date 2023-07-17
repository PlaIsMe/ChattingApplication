package com.chattingapplication.springbootserver.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chattingapplication.springbootserver.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.lastName LIKE %?1% OR u.firstName LIKE %?1%")
    List<UserEntity> searchUser(String keyword);
}
