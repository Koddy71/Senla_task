package ru.ilya.autoconfig;

import ru.ilya.autodi.ConfigProperty;

public class AppConfig {
    @ConfigProperty(propertyName = "room.status.change.enable")
    private boolean roomStatusChangeEnable;

    @ConfigProperty(propertyName = "room.history.limit")
    private int roomHistoryLimit;

    @ConfigProperty(propertyName = "storage.type")
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
