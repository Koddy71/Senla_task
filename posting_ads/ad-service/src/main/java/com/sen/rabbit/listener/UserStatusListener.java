package com.sen.rabbit.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sen.rabbit.config.RabbitMqConfig;
import com.sen.rabbit.event.UserBlockedEvent;
import com.sen.rabbit.event.UserUnblockedEvent;
import com.sen.service.AdService;

@Component
public class UserStatusListener {

    private final AdService adService;

    public UserStatusListener(AdService adService) {
        this.adService = adService;
    }

    @RabbitListener(queues = RabbitMqConfig.USER_BLOCKED_QUEUE)
    public void onUserBlocked(UserBlockedEvent event) {
        adService.archiveAdsByUserBlock(event.getUserId());
    }

    @RabbitListener(queues = RabbitMqConfig.USER_UNBLOCKED_QUEUE)
    public void onUserUnblocked(UserUnblockedEvent event) {
        adService.restoreAdsAfterUserUnblock(event.getUserId());
    }
}