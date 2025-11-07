package ru.ilya;

import java.time.LocalDate;
import java.util.List;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.model.Service;
import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.service.impl.GuestServiceImpl;
import ru.ilya.service.impl.RoomServiceImpl;
import ru.ilya.service.impl.ServiceManagerImpl;

public class HotelAdmin {

	public static void main(String[] args) {

		RoomService roomService = new RoomServiceImpl();
		ServiceManager serviceManager = new ServiceManagerImpl();
		GuestService guestService = new GuestServiceImpl(roomService, serviceManager);

		roomService.addRoom(new Room(101, 1500, 2, 3));
		roomService.addRoom(new Room(102, 2000, 3, 4));
		roomService.addRoom(new Room(103, 5000, 4, 5));
		roomService.addRoom(new Room(104, 3000, 2, 5));

		roomService.changeStatus(102, RoomStatus.MAINTENANCE);

		System.out.println("--- Номера ---");
		for (Room room : roomService.getAllRooms()) {
			System.out.println(room.getInfo());
		}
		roomService.changeStatus(102, RoomStatus.AVAILABLE);

		serviceManager.addService(new Service("Завтрак", 300));
		serviceManager.addService(new Service("SPA", 1200));
		serviceManager.addService(new Service("Бассейн", 800));

		serviceManager.changePrice("SPA", 1000);
		
		System.out.println("\n--- Услуги ---");
		for (Service service : serviceManager.getAllServices()) {
			System.out.println(service.getInfo());
		}

		Guest ivanov = guestService.checkIn("Иванов", 101, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 5));
		Guest petrov = guestService.checkIn("Петров", 104, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 5));


		System.out.println("\n--- Постояльцы ---");
		List<Guest> guests = guestService.getAllGuests();
		for (Guest g : guests) {
			System.out.println(g.getInfo());
		}

		if (ivanov != null) {
			Service spa = serviceManager.findService("SPA");
			Service breakfast = serviceManager.findService("Завтрак");
			if (spa != null)
				ivanov.addService(spa);
			if (breakfast != null)
				ivanov.addService(breakfast);
		}

		if (ivanov != null) {
			System.out.println("\nСумма оплаты Иванова: " + ivanov.getTotalCost());
		}

		System.out.println("\n=== Свободные номера ===");
		for (Room room : roomService.getFreeRooms()) {
			System.out.println(room.getInfo());
		}

		System.out.println("\n=== Номера по звёздам ===");
		for (Room room : roomService.getRoomsSorted("stars")) {
			System.out.println(room.getInfo());
		}


		Room r101 = roomService.findRoom(101);
		if (r101 != null) {
			List<Guest> lastStays = r101.getLastStays(3);
			System.out.println("\n--- Последние проживания номера 101 ---");
			for (Guest s : lastStays) {
					System.out.println(s.getStayInfo());
			}
		}
	}
}
