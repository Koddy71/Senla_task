package com.example;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.producer.ProducerInitializer;

public class BankLauncher {
    public static void main(String[] args) throws InterruptedException {
        String mode = System.getenv().getOrDefault("APP_MODE", "producer");
        if (!mode.equals("producer") && !mode.equals("consumer")) {
            throw new IllegalArgumentException("APP_MODE must be producer or consumer");
        }

        System.setProperty("spring.profiles.active", mode);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        Runtime.getRuntime().addShutdownHook(new Thread(ctx::close)); //корректное завершение работы

        if (Objects.equals(mode, "producer")) {
            ctx.getBean(ProducerInitializer.class).initAccounts();
        }

        new CountDownLatch(1).await();  //бесконечное ожидание
    }
}
