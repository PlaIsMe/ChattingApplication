package com.chattingapplication.springbootserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chattingapplication.springbootserver.entity.AccountEntity;


public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByEmail(String email);
}
