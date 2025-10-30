package service.impl;

import model.Room;
import model.RoomStatus;
import model.Service;
import service.RoomService;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class RoomServiceImpl implements RoomService {
	private Map<Integer, Room> rooms = new HashMap<>();

	@Override
	public void addRoom(Room room) {
		rooms.put(room.getNumber(), room);
	}

	@Override
	public Room findRoom(int number) {
		return rooms.get(number); 
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

		public boolean changePrice(int number, int newPrice) {
		Room room = findRoom(number);
		if (room != null && newPrice > 0) {
			room.setPrice(newPrice);
			return true;
		}
		return false;
	}

	@Override
	public List<Room> getAllRooms() {
		return new ArrayList<>(rooms.values());
	}
}
