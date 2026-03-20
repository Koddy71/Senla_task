package ru.ilya.model;

public enum RoomStatus {
    ACTIVE("Свободен"),
    MAINTENANCE("На ремонте");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
