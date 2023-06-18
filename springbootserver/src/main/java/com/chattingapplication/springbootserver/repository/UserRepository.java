package com.chattingapplication.springbootserver.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.chattingapplication.springbootserver.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
