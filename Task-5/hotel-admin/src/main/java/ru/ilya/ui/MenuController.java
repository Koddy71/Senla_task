package ru.ilya.ui;

import ru.ilya.service.GuestService;
import ru.ilya.service.PriceService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.controller.*;
public class MenuController {
	private final Menu rootMenu;

	public MenuController(GuestService guestService, RoomService roomService, ServiceManager serviceManager, PriceService priceService) {
		GuestController guestController = new GuestController(guestService);
		RoomController roomController = new RoomController(roomService);
		ServiceController serviceController = new ServiceController(serviceManager);
		PriceController priceController = new PriceController(priceService);
		MenuBuilder builder = new MenuBuilder(guestController, roomController, serviceController, priceController);
		this.rootMenu = builder.buildMainMenu();
	}

	public void run() {
		Navigator nav = Navigator.getInstance(rootMenu);
		nav.start();
	}
}
