package ru.ilya.model;

public enum RoomStatus {
    AVAILABLE("Свободен"),
    OCCUPIED("Занят"),
    MAINTENANCE("На ремонте"),
    RESERVED("Забронирован");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

