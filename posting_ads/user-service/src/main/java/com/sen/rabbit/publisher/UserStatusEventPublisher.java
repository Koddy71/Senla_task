package com.sen.rabbit.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.sen.rabbit.config.RabbitMqConfig;
import com.sen.rabbit.event.UserBlockedEvent;
import com.sen.rabbit.event.UserUnblockedEvent;

@Component
public class UserStatusEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserStatusEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserBlocked(UserBlockedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EVENTS_EXCHANGE,
                RabbitMqConfig.USER_BLOCKED_ROUTING_KEY,
                event);
    }

    public void publishUserUnblocked(UserUnblockedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EVENTS_EXCHANGE,
                RabbitMqConfig.USER_UNBLOCKED_ROUTING_KEY,
                event);
    }
}