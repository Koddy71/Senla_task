package ru.ilya.dto;

import java.time.LocalDate;

public class GuestRequest {
    private String name;    //Тут должны аннотации висеть для проверки тела запроса
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
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
}
