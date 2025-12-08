package ru.ilya.ui;

import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.state.ProgramState;
import ru.ilya.state.StateManager;

public class MenuController {
	private final Navigator navigator;
   private final Builder builder;
   private final RoomService roomService;
   private final GuestService guestService;
   private final ServiceManager serviceManager;


	public MenuController(Builder builder, RoomService roomService, GuestService guestService, ServiceManager serviceManager) {
		this.builder=builder;
      this.builder.buildConsoleMenu();
      this.navigator=Navigator.getInstance(builder.getRootMenu());

      this.roomService = roomService;
      this.guestService = guestService;
      this.serviceManager = serviceManager;
	}

   private void saveState() {
    ProgramState state = new ProgramState(
        guestService.getAllGuests(),
        roomService.getAllRooms(),
        serviceManager.getAllServices()
    );

    StateManager.save(state);
    System.out.println("Состояние программы сохранено.");
   }

	public void run() {
		navigator.start();
      saveState();
   }
}
