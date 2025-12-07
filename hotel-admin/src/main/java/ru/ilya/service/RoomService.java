package ru.ilya.service;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

   boolean addRoom(Room room);

   boolean removeRoom(int number);

   Room findRoom(int number);

   boolean checkIn(int number);

   boolean checkOut(int number);

   boolean changeStatus(int number, RoomStatus status);

   boolean changePrice(int number, int newPrice);

   List<Room> getAllRooms();

   List<Room> getFreeRooms();

   int countFreeRooms();

   List<Room> getRoomsFreeByDate(LocalDate date);

   List<Room> getRoomsSorted(String sortBy);
}
