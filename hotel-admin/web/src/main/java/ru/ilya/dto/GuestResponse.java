package ru.ilya.dto;

import java.time.LocalDate;
import java.util.List;

public class GuestResponse {
    private int id;
    private String name;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<String> services;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public List<String> getServices(){
        return services;
    }

    public void setServices(List<String> services){
        this.services=services;
    }

}
