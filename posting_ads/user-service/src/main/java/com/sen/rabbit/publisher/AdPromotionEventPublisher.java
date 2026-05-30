package com.sen.rabbit.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.sen.rabbit.config.RabbitMqConfig;
import com.sen.rabbit.event.AdPromotionRequestedEvent;

@Component
public class AdPromotionEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(AdPromotionEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public AdPromotionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(AdPromotionRequestedEvent event) {
        logger.info("Публикация события payment.processed: paymentId={}, adId={}, hours={}, userLogin={}",
                event.getPaymentId(), event.getAdId(), event.getHours(), event.getUserLogin());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EVENTS_EXCHANGE,
                RabbitMqConfig.PAYMENT_PROCESSED_ROUTING_KEY,
                event);
    }
}