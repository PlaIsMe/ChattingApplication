package com.chattingapplication.springbootserver.service.implement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.chattingapplication.springbootserver.entity.AccountEntity;
import com.chattingapplication.springbootserver.entity.UserEntity;
import com.chattingapplication.springbootserver.model.Account;
import com.chattingapplication.springbootserver.model.User;
import com.chattingapplication.springbootserver.repository.AccountRepository;
import com.chattingapplication.springbootserver.repository.UserRepository;
import com.chattingapplication.springbootserver.service.interfaces.AccountService;
import com.chattingapplication.springbootserver.service.interfaces.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    
    @Override
    public List<User> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities
            .stream().map(user -> new User(
                user.getId(),
                user.getLastName(),
                user.getFirstName(),
                user.getAvatar(),
                user.getGender(),
                user.getDob()
            )).collect(Collectors.toList());
    }

    @Override
    public User createUser(User user) throws Exception {
        try {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(user, userEntity);
            user.setId(userRepository.save(userEntity).getId());
            return user;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean deleteUser(Long userId) throws Exception {
        // try {
        //     AccountEntity accountEntity = accountRepository.findById(accountId).get();
        //     accountRepository.delete(accountEntity);
            return true;
        // } catch (NoSuchElementException e) {
        //     throw new Exception("Account not found!");
        // }
    }

    @Override
    public User updateUser(Long userId, User user) throws Exception {
        try {
            // AccountEntity accountEntity = accountRepository.findById(accountId).get();
            // if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
            //     throw new Exception("email taken");
            // }
            // accountEntity.setPassword(account.getPassword());
            // accountEntity.setEmail(account.getEmail());
            // accountEntity.setUpdatedAt(LocalDateTime.now());
            // accountRepository.save(accountEntity);
            // account.setCreatedAt(accountEntity.getCreatedAt());
            // account.setUpdatedAt(accountEntity.getUpdatedAt());
            // account.setId(accountId);
            return user;
        } catch (NoSuchElementException e) {
            throw new Exception("Account not found!");
        }
    }

    @Override
    public User getUserById(Long userId) throws Exception {
        try {
            // AccountEntity accountEntity = accountRepository.findById(accountId).get();
            User user = new User();
            // BeanUtils.copyProperties(accountEntity, account);
            return user;
        } catch (NoSuchElementException e) {
            throw new Exception("Account not found!");
        }
    }
}
