package com.example.consumer;

import com.example.model.TransferMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("consumer")
public class TransferListener {
    private static final Logger log = LoggerFactory.getLogger(TransferListener.class);
    private final TransferProcessor processor;

    public TransferListener(TransferProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${app.kafka.group-id}", containerFactory = "batchListenerContainerFactory")
    public void listen(List<TransferMessage> batch, Acknowledgment ack) {
        for (TransferMessage msg : batch) {
            log.info("Consumer begin: id={}", msg.getId());
            processor.process(msg);
        }
        ack.acknowledge();
    }
}