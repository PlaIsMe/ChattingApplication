package com.chattingapplication.springbootserver.service.implement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.chattingapplication.springbootserver.entity.AccountEntity;
import com.chattingapplication.springbootserver.model.Account;
import com.chattingapplication.springbootserver.repository.AccountRepository;
import com.chattingapplication.springbootserver.service.interfaces.AccountService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    
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
                account.getUser()
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
            BeanUtils.copyProperties(account, accountEntity);
            accountRepository.save(accountEntity);
            account.setId(accountRepository.findByEmail(account.getEmail()).get().getId());
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
            BeanUtils.copyProperties(accountEntity, account);
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
                if (accountRepository.loginAccount(account.getEmail(), accountEntity.getPassword()).isPresent()) {
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
