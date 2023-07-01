package com.chattingapplication.springbootserver.service.implement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.chattingapplication.springbootserver.entity.AccountEntity;
import com.chattingapplication.springbootserver.entity.UserEntity;
import com.chattingapplication.springbootserver.model.Account;
import com.chattingapplication.springbootserver.model.ChatRoom;
import com.chattingapplication.springbootserver.model.User;
import com.chattingapplication.springbootserver.repository.AccountRepository;
import com.chattingapplication.springbootserver.service.interfaces.AccountService;
import com.chattingapplication.springbootserver.service.interfaces.ChatRoomService;
import com.chattingapplication.springbootserver.service.interfaces.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    
    @Override
    public List<Account> getAllAccounts() {
        List<AccountEntity> accountsEntities = accountRepository.findAll();
        return accountsEntities
            .stream().map(account -> new Account(
                account.getId(),
                account.getEmail(),
                account.getPassword(),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                new User(
                    account.getUser().getId(),
                    account.getUser().getFirstName(),
                    account.getUser().getLastName(),
                    account.getUser().getGender(),
                    account.getUser().getAvatar(),
                    account.getUser().getDob(),
                    chatRoomService.getChatRoomsByUserId(account.getUser().getId())
                )
            )).collect(Collectors.toList());
    }

    @Override
    public Account createAccount(Account account) throws Exception {
        try {
            AccountEntity accountEntity = new AccountEntity();
            if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
                throw new Exception("email taken");
            }
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            User newUser = userService.createUser(new User());
            UserEntity newUserEntity = new UserEntity();
            BeanUtils.copyProperties(newUser, newUserEntity);
            BeanUtils.copyProperties(account, accountEntity);
            accountEntity.setUser(newUserEntity);
            account.setUser(newUser);
            account.setId(accountRepository.save(accountEntity).getId());
            return account;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean deleteAccount(Long accountId) throws Exception {
        try {
            AccountEntity accountEntity = accountRepository.findById(accountId).get();
            accountRepository.delete(accountEntity);
            return true;
        } catch (NoSuchElementException e) {
            throw new Exception("Account not found!");
        }
    }

    @Override
    public Account updateAccount(Long accountId, Account account) throws Exception {
        try {
            AccountEntity accountEntity = accountRepository.findById(accountId).get();
            if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
                throw new Exception("email taken");
            }
            accountEntity.setPassword(account.getPassword());
            accountEntity.setEmail(account.getEmail());
            accountEntity.setUpdatedAt(LocalDateTime.now());
            accountRepository.save(accountEntity);
            account.setCreatedAt(accountEntity.getCreatedAt());
            account.setUpdatedAt(accountEntity.getUpdatedAt());
            account.setId(accountId);
            return account;
        } catch (NoSuchElementException e) {
            throw new Exception("Account not found!");
        }
    }

    @Override
    public Account getAccountById(Long accountId) throws Exception {
        try {
            AccountEntity accountEntity = accountRepository.findById(accountId).get();
            Account account = new Account();
            User user = new User();
            BeanUtils.copyProperties(accountEntity.getUser(), user);
            user.setChatRooms(chatRoomService.getChatRoomsByUserId(user.getId()));
            BeanUtils.copyProperties(accountEntity, account);
            account.setUser(user);
            return account;
        } catch (NoSuchElementException e) {
            throw new Exception("Account not found!");
        }
    }

    @Override
    public Account loginAccount(Account account) throws Exception {
        try {
            AccountEntity accountEntity = new AccountEntity();
            BeanUtils.copyProperties(account, accountEntity);
            if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
                Optional<AccountEntity> accountEngtityOptional = accountRepository.loginAccount(account.getEmail(), accountEntity.getPassword());
                if (accountEngtityOptional.isPresent()) {
                    accountEntity = accountEngtityOptional.get();
                    BeanUtils.copyProperties(accountEntity, account);
                    User user = new User();
                    BeanUtils.copyProperties(accountEntity.getUser(), user);
                    user.setChatRooms(chatRoomService.getChatRoomsByUserId(user.getId()));
                    account.setUser(user);
                    return account;
                } else {
                    throw new Exception("Wrong password!");
                }
            } else {
                throw new Exception("Email not found!");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


}
