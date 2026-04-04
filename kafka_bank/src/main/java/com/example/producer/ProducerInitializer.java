package com.example.producer;

import com.example.model.Account;
import com.example.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            accounts = IntStream.rangeClosed(1, 1000)
                    .<Account>mapToObj(i -> Account.builder()
                            .id((long) i)
                            .balance(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(100_00, 1_000_001), 2))
                            .build())
                    .collect(Collectors.toList());
            accountRepository.saveAll(accounts);
        }

        accountCache.load(accounts);
    }
}