package com.example.consumer;

import com.example.model.TransferMessage;
import com.example.repository.MoneyTransferRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("consumer")
public class TransferProcessor {
    private static final Logger log = LoggerFactory.getLogger(TransferProcessor.class);
    private final TransferTxService txService;
    private final FailedTransferWriter failedTransferWriter;
    private final MoneyTransferRepository moneyTransferRepository;

    public TransferProcessor(TransferTxService txService,
            FailedTransferWriter failedTransferWriter,
            MoneyTransferRepository moneyTransferRepository) {
        this.txService = txService;
        this.failedTransferWriter = failedTransferWriter;
        this.moneyTransferRepository = moneyTransferRepository;
    }

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