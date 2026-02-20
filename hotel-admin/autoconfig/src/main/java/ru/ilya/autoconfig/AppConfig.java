package ru.ilya.autoconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    @Value("${room.status.change.enable}")
    private boolean roomStatusChangeEnable;

    @Value("${room.history.limit}")
    private int roomHistoryLimit;

    @Value("${storage.type}")
    private String storageType;

    public boolean isRoomStatusChangeEnable() {
        return roomStatusChangeEnable;
    }

    public int getRoomHistoryLimit() {
        return roomHistoryLimit;
    }

    public String getStorageType() {
        return storageType;
    }
}
