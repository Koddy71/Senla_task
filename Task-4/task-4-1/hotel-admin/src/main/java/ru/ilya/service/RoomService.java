package ru.ilya.service;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
	void addRoom(Room room);

	Room findRoom(int number);

	boolean checkIn(int roomNumber);

	boolean checkOut(int roomNumber);

	boolean changeStatus(int roomNumber, RoomStatus status);

	boolean changePrice(int number, int newPrice);

	List<Room> getAllRooms();

	List<Room> getFreeRooms();

	int countFreeRooms();

	List<Room> getRoomsFreeByDate(LocalDate date);

	List<Room> getRoomsSorted(String sortBy);
}