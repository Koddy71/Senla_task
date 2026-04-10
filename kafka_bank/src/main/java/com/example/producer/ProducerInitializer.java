package com.example.producer;

import com.example.model.Account;
import com.example.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Profile("producer")
public class ProducerInitializer {

    private final AccountRepository accountRepository;
    private final AccountCache accountCache;

    public ProducerInitializer(AccountRepository accountRepository, AccountCache accountCache) {
        this.accountRepository = accountRepository;
        this.accountCache = accountCache;
    }

    @Transactional
    public void initAccounts() {
        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) {
            accounts = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                Account account = new Account();
                account.setId((long) i);
                account.setBalance(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(100_00, 1_000_001), 2));
                accounts.add(account);
            }
            accountRepository.saveAll(accounts);
        }

        accountCache.load(accounts);
    }
}