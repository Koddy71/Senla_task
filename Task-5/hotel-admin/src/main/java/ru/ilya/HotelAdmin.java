package ru.ilya;

import ru.ilya.controller.GuestController;
import ru.ilya.controller.PriceController;
import ru.ilya.controller.RoomController;
import ru.ilya.controller.ServiceController;
import ru.ilya.service.GuestService;
import ru.ilya.service.PriceService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.service.impl.GuestServiceImpl;
import ru.ilya.service.impl.PriceServiceImpl;
import ru.ilya.service.impl.RoomServiceImpl;
import ru.ilya.service.impl.ServiceManagerImpl;
import ru.ilya.ui.Builder;
import ru.ilya.ui.MenuBuilder;
import ru.ilya.ui.MenuController;

public class HotelAdmin {
	public static void main(String[] args) {
		RoomService roomService = RoomServiceImpl.getInstance(); 
		ServiceManager serviceManager = ServiceManagerImpl.getInstance();
		GuestService guestService = GuestServiceImpl.getInstance(roomService, serviceManager);
		PriceService priceService = PriceServiceImpl.getInstance(roomService, serviceManager);

      GuestController guestController = GuestController.getInstance(guestService);
		RoomController roomController = RoomController.getInstance(roomService);
		ServiceController serviceController = ServiceController.getInstance(serviceManager);
		PriceController priceController = PriceController.getInstance(priceService);

		MenuBuilder factory = new MenuBuilder(guestController, roomController, serviceController, priceController);
      
      Builder builder = new Builder(factory);

      MenuController menuController = new MenuController(builder);
		menuController.run();
	}
}
