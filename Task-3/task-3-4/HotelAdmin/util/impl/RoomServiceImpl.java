package util.impl;

import model.Room;
import model.RoomStatus;
import util.RoomService;

import java.util.ArrayList;
import java.util.List;

public class RoomServiceImpl implements RoomService {

	private List<Room> rooms = new ArrayList<>();

	@Override
	public void addRoom(Room room) {
		rooms.add(room);
	}

	@Override
	public Room findRoom(int number) {
		return rooms.stream()
				.filter(r -> r.getNumber() == number)
				.findFirst()
				.orElse(null);
	}

	@Override
	public boolean checkIn(int roomNumber) {
		Room room = findRoom(roomNumber);
		if (room != null && room.getStatus() == RoomStatus.AVAILABLE) {
			room.setStatus(RoomStatus.OCCUPIED);
			return true;
		}
		return false;
	}

	@Override
	public boolean checkOut(int roomNumber) {
		Room room = findRoom(roomNumber);
		if (room != null && room.getStatus() == RoomStatus.OCCUPIED) {
			room.setStatus(RoomStatus.AVAILABLE);
			return true;
		}
		return false;
	}

	@Override
	public boolean changeStatus(int roomNumber, RoomStatus status) {
		Room room = findRoom(roomNumber);
		if (room != null) {
			room.setStatus(status);
			return true;
		}
		return false;
	}

	@Override
	public List<Room> getAllRooms() {
		return rooms;
	}
}
