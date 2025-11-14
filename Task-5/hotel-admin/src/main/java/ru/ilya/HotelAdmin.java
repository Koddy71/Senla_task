package ru.ilya;

import ru.ilya.service.GuestService;
import ru.ilya.service.PriceService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.service.impl.GuestServiceImpl;
import ru.ilya.service.impl.PriceServiceImpl;
import ru.ilya.service.impl.RoomServiceImpl;
import ru.ilya.service.impl.ServiceManagerImpl;
import ru.ilya.ui.MenuController;

public class HotelAdmin {
	public static void main(String[] args) {
		RoomService roomService = new RoomServiceImpl(); 
		ServiceManager serviceManager = new ServiceManagerImpl();
		GuestService guestService = new GuestServiceImpl(roomService, serviceManager);
		PriceService priceService = new PriceServiceImpl(roomService, serviceManager);

		MenuController menuController = new MenuController(guestService, roomService, serviceManager, priceService);
		menuController.run();
	}
}
