package com.sen.rabbit.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sen.rabbit.config.RabbitMqConfig;
import com.sen.rabbit.event.AdPromotionRequestedEvent;
import com.sen.service.AdService;

@Component
public class AdPromotionListener {

    private static final Logger logger = LoggerFactory.getLogger(AdPromotionListener.class);

    private final AdService adService;

    public AdPromotionListener(AdService adService) {
        this.adService = adService;
    }

    @RabbitListener(queues = RabbitMqConfig.PAYMENT_PROCESSED_QUEUE)
    public void onPaymentProcessed(AdPromotionRequestedEvent event) {
        logger.info("Получено событие payment.processed: adId={}, paymentId={}, hours={}, userLogin={}",
                event.getAdId(), event.getPaymentId(), event.getHours(), event.getUserLogin());

        try {
            adService.promoteAd(event.getAdId(), event.getHours());
            logger.info("Объявление {} успешно продвинуто по событию payment.processed", event.getAdId());
        } catch (Exception ex) {
            logger.error("Ошибка обработки события payment.processed для adId={}: {}",
                    event.getAdId(), ex.getMessage(), ex);
        }
    }
}