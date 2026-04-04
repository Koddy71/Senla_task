package com.example.producer;

import com.example.model.Account;
import com.example.model.TransferMessage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountCache {
    private final Map<Long, BigDecimal> balances = new LinkedHashMap<>();

    public synchronized void load(List<Account> accounts) {
        balances.clear();
        for (Account a : accounts) {
            balances.put(a.getId(), a.getBalance());
        }
    }

    public synchronized boolean isEmpty() {
        return balances.isEmpty();
    }

    public synchronized TransferMessage nextTransfer() {
        List<Long> ids = new ArrayList<>(balances.keySet());
        if (ids.size() < 2) {
            throw new IllegalStateException("Нужно минимум 2 счёта");
        }

        Long fromId;
        Long toId;
        BigDecimal fromBalance;

        while (true) {
            fromId = ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
            toId = ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
            if (fromId.equals(toId))
                continue;

            fromBalance = balances.get(fromId);
            if (fromBalance != null && fromBalance.compareTo(new BigDecimal("1.00")) >= 0)
                break;
        }

        long maxCents = fromBalance.movePointRight(2).longValue();
        long cents = ThreadLocalRandom.current().nextLong(100, maxCents + 1);
        BigDecimal amount = BigDecimal.valueOf(cents, 2);

        balances.put(fromId, balances.get(fromId).subtract(amount));
        balances.put(toId, balances.get(toId).add(amount));

        return new TransferMessage(UUID.randomUUID().toString(), fromId, toId, amount);
    }
}