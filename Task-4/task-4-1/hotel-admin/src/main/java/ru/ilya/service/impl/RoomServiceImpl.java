package ru.ilya.service.impl;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;

import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	@Override
	public List<Room> getFreeRooms(){
		List<Room> freeRooms = new ArrayList<>();
		for (Room r : rooms.values()){
			if(r.getStatus()==RoomStatus.AVAILABLE){
				freeRooms.add(r);
			}
		}
		return freeRooms;
	}

	@Override
	public int countFreeRooms() {
		int count=0;
		for (Room r : rooms.values()) {
			if (r.getStatus() == RoomStatus.AVAILABLE) {
				count++;
			}
		}
		return count;
	}

	@Override
	public List<Room> getRoomsFreeByDate(LocalDate date) {
		List<Room> freeByDate = new ArrayList<>();
		for (Room r : rooms.values()) {
			if (r.isFreeOn(date)) {
					freeByDate.add(r);
			}
		}
		return freeByDate;
	}

	@Override
	public List<Room> getRoomsSortedByPrice() {
		List<Room> sorted = new ArrayList<>(rooms.values());
		sorted.sort(Comparator.comparingDouble(Room::getPrice));
		Collections.reverse(sorted);
		return sorted; 
	}

	@Override
	public List<Room> getRoomsSortedByCapacity() {
		List<Room> sorted = new ArrayList<>(rooms.values());
		sorted.sort(Comparator.comparingInt(Room::getCapacity));
		Collections.reverse(sorted);
		return sorted;
	}

	@Override
	public List<Room> getRoomsSortedByStars() {
		List<Room> sorted = new ArrayList<>(rooms.values());
		sorted.sort(Comparator.comparingInt(Room::getStars));
		Collections.reverse(sorted);
		return sorted;
	}
}
