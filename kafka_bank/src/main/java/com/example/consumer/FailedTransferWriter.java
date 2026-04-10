package com.example.consumer;

import com.example.model.Account;
import com.example.model.MoneyTransfer;
import com.example.model.TransferMessage;
import com.example.model.TransferStatus;
import com.example.repository.AccountRepository;
import com.example.repository.MoneyTransferRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("consumer")
public class FailedTransferWriter {

    private final MoneyTransferRepository moneyTransferRepository;
    private final AccountRepository accountRepository;

    public FailedTransferWriter(MoneyTransferRepository moneyTransferRepository,
            AccountRepository accountRepository) {
        this.moneyTransferRepository = moneyTransferRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailed(TransferMessage msg) {
        if (moneyTransferRepository.existsById(msg.getId())) {
            return;
        }

        Account from = accountRepository.findById(msg.getFromAccountId())
                .orElseThrow(() -> new IllegalStateException("Source account not found"));
        Account to = accountRepository.findById(msg.getToAccountId())
                .orElseThrow(() -> new IllegalStateException("Target account not found"));

        MoneyTransfer transfer = new MoneyTransfer(msg.getId(), from, to, msg.getAmount(), TransferStatus.FAILED);
        moneyTransferRepository.save(transfer);
    }
}