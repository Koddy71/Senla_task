package com.example.producer;

import com.example.model.TransferMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Profile("producer")
public class TransferProducer {
    private static final Logger log = LoggerFactory.getLogger(TransferProducer.class);
    private final KafkaTemplate<String, TransferMessage> kafkaTemplate;
    private final AccountCache accountCache;
    private final AtomicInteger partitionCounter = new AtomicInteger(0);

    @Value("${app.kafka.topic}")
    private String topic;

    public TransferProducer(KafkaTemplate<String, TransferMessage> kafkaTemplate, AccountCache accountCache) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountCache = accountCache;
    }

    @Scheduled(fixedDelayString = "${app.producer.delay-ms}")
    public void generateAndSend() {
        TransferMessage message = accountCache.nextTransfer();
        int partition = Math.floorMod(partitionCounter.getAndIncrement(), 3);

        log.info("Producer send: id={}, partition={}, time={}", message.getId(), partition, Instant.now());

        kafkaTemplate.executeInTransaction(ops -> {
            ops.send(new ProducerRecord<>(topic, partition, message.getId(), message));
            return null;
        });
    }
}