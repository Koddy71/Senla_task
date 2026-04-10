package com.example.consumer;

import com.example.model.Account;
import com.example.model.MoneyTransfer;
import com.example.model.TransferMessage;
import com.example.model.TransferStatus;
import com.example.repository.AccountRepository;
import com.example.repository.MoneyTransferRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("consumer")
public class TransferTxService {

    private final AccountRepository accountRepository;
    private final MoneyTransferRepository moneyTransferRepository;

    public TransferTxService(AccountRepository accountRepository, MoneyTransferRepository moneyTransferRepository){
        this.accountRepository=accountRepository;
        this.moneyTransferRepository=moneyTransferRepository;
    }

    @Transactional
    public void apply(TransferMessage msg) {
        if (moneyTransferRepository.existsById(msg.getId())) {
            return;
        }

        long fromId = msg.getFromAccountId();  // Определяем порядок блокировки счетов
        long toId = msg.getToAccountId();
        long firstId, secondId;
        if (fromId < toId) {
            firstId = fromId;
            secondId = toId;
        } else {
            firstId = toId;
            secondId = fromId;
        }

        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new IllegalStateException("Счёт не найден: " + firstId));
        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new IllegalStateException("Счёт не найден: " + secondId));

        Account from, to;   // Определяем отправителя и получателя
        if (first.getId() == fromId) {
            from = first;
            to = second;
        } else {
            from = second;
            to = first;
        }

        if (from.getId().equals(to.getId())) {
            throw new IllegalStateException("Отправитель и получатель — это одно и то же");
        }
        if (from.getBalance().compareTo(msg.getAmount()) < 0) {
            throw new IllegalStateException("Недостаточно средств");
        }

        from.setBalance(from.getBalance().subtract(msg.getAmount()));
        to.setBalance(to.getBalance().add(msg.getAmount()));

        MoneyTransfer transfer = new MoneyTransfer(msg.getId(), from, to, msg.getAmount(), TransferStatus.READY);

        accountRepository.save(from);
        accountRepository.save(to);
        moneyTransferRepository.save(transfer);
    }
}