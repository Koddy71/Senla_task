package com.sen.rabbit.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String EVENTS_EXCHANGE = "marketplace.events";

    public static final String PAYMENT_PROCESSED_QUEUE = "ad-service.payment-processed";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";

    public static final String USER_BLOCKED_QUEUE = "ad-service.user-blocked";
    public static final String USER_UNBLOCKED_QUEUE = "ad-service.user-unblocked";

    public static final String USER_BLOCKED_ROUTING_KEY = "user.blocked";
    public static final String USER_UNBLOCKED_ROUTING_KEY = "user.unblocked";

    @Bean
    public ConnectionFactory rabbitConnectionFactory(
            @Value("${rabbitmq.host}") String host,
            @Value("${rabbitmq.port}") int port,
            @Value("${rabbitmq.username}") String username,
            @Value("${rabbitmq.password}") String password) {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory rabbitConnectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory rabbitConnectionFactory) {
        return new RabbitAdmin(rabbitConnectionFactory);
    }

    @Bean
    public TopicExchange marketplaceEventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentProcessedQueue() {
        return QueueBuilder.durable(PAYMENT_PROCESSED_QUEUE).build();
    }

    @Bean
    public Queue userBlockedQueue() {
        return QueueBuilder.durable(USER_BLOCKED_QUEUE).build();
    }

    @Bean
    public Queue userUnblockedQueue() {
        return QueueBuilder.durable(USER_UNBLOCKED_QUEUE).build();
    }

    @Bean
    public Binding paymentProcessedBinding(Queue paymentProcessedQueue, TopicExchange marketplaceEventsExchange) {
        return BindingBuilder.bind(paymentProcessedQueue)
                .to(marketplaceEventsExchange)
                .with(PAYMENT_PROCESSED_ROUTING_KEY);
    }

    @Bean
    public Binding userBlockedBinding(Queue userBlockedQueue, TopicExchange marketplaceEventsExchange) {
        return BindingBuilder.bind(userBlockedQueue)
                .to(marketplaceEventsExchange)
                .with(USER_BLOCKED_ROUTING_KEY);
    }

    @Bean
    public Binding userUnblockedBinding(Queue userUnblockedQueue, TopicExchange marketplaceEventsExchange) {
        return BindingBuilder.bind(userUnblockedQueue)
                .to(marketplaceEventsExchange)
                .with(USER_UNBLOCKED_ROUTING_KEY);
    }
}