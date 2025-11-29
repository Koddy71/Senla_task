package ru.ilya.service;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

   boolean addRoom(Room room);

   boolean removeRoom(int id);

   Room findRoom(int id);

   boolean checkIn(int id);

   boolean checkOut(int id);

   boolean changeStatus(int id, RoomStatus status);

   boolean changePrice(int id, int newPrice);

   List<Room> getAllRooms();

   List<Room> getFreeRooms();

   int countFreeRooms();

   List<Room> getRoomsFreeByDate(LocalDate date);

   List<Room> getRoomsSorted(String sortBy);
}
