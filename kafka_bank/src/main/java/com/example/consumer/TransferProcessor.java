package com.example.consumer;

import com.example.model.TransferMessage;
import com.example.repository.MoneyTransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("consumer")
public class TransferProcessor {

    private final TransferTxService txService;
    private final FailedTransferWriter failedTransferWriter;
    private final MoneyTransferRepository moneyTransferRepository;

    public void process(TransferMessage msg) {
        if (moneyTransferRepository.existsById(msg.getId())) {
            return;
        }

        try {
            txService.apply(msg);
            log.info("Consumer success: id={}", msg.getId());
        } catch (IllegalStateException validationError) {
            log.error("Consumer validation error: id={}, msg={}", msg.getId(), validationError.getMessage());
        } catch (Exception txError) {
            log.error("Consumer transaction error: id={}", msg.getId(), txError);
            failedTransferWriter.saveFailed(msg);
        }
    }
}