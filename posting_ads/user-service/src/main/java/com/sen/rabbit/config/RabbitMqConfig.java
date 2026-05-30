package com.sen.rabbit.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Bean
    public ConnectionFactory rabbitConnectionFactory( // соединение с rabbit
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
    public RabbitTemplate rabbitTemplate(ConnectionFactory rabbitConnectionFactory, // отправка сообщений в обменник
            Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange marketplaceEventsExchange() {              // обменник
        return new TopicExchange(EVENTS_EXCHANGE, true, false);     // true - обменник останется после перезапуска брокера
                                                                    // false - обменник не удалится, когда отвяжут все очереди
    }
}