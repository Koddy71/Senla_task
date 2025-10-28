package util;

import model.Room;
import model.RoomStatus;

import java.util.List;

public interface RoomService {
	void addRoom(Room room);

	Room findRoom(int number);

	boolean checkIn(int roomNumber);

	boolean checkOut(int roomNumber);

	boolean changeStatus(int roomNumber, RoomStatus status);

	List<Room> getAllRooms();
}