package ru.ilya.service.impl;

import ru.ilya.model.Room;
import ru.ilya.service.PriceService;
import ru.ilya.service.RoomService;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriceServiceImpl implements PriceService{
	private final RoomService roomService;
	private final ServiceManager serviceManager;

	public PriceServiceImpl(RoomService roomService, ServiceManager serviceManager) {
		this.roomService = roomService;
		this.serviceManager = serviceManager;
	}

	public List<String> getRoomsAndServices(String orderBy) {
		List<String> result = new ArrayList<>();

		List<Room> roomList = new ArrayList<>(roomService.getAllRooms());
		List<Service> serviceList = new ArrayList<>(serviceManager.getAllServices());

		roomList.sort(Comparator.comparingInt(Room::getPrice));
		serviceList.sort(Comparator.comparingInt(Service::getPrice));

		if ("room".equalsIgnoreCase(orderBy)) {
			for (Room room : roomList) {
				result.add("Номер " + room.getNumber() + ": " + room.getPrice() + " руб.");
			}
			for (Service service : serviceList) {
				result.add(service.getInfo());
			}
		} else if ("service".equalsIgnoreCase(orderBy)) {
			for (Service service : serviceList) {
				result.add(service.getInfo());
			}
			for (Room room : roomList) {
				result.add("Номер " + room.getNumber() + ": " + room.getPrice() + " руб.");
			}
		} else {
			result.add("Ошибка: неверный ввод. Используйте 'room' или 'service'.");
		}

		return result;
	}
}