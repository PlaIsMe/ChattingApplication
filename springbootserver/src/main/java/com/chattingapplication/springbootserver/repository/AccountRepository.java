package com.chattingapplication.springbootserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chattingapplication.springbootserver.entity.AccountEntity;


public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByEmail(String email);
    @Query("SELECT a FROM AccountEntity a WHERE a.email = ?1 and a.password = ?2")
    Optional<AccountEntity> loginAccount(String email, String password);
}
