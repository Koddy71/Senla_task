package ru.ilya.ui;

import java.util.List;

import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.*;
import ru.ilya.autodi.Inject;
import ru.ilya.controller.ImportExportController;

public class MenuController {

   @Inject
   private Builder builder;

   @Inject
   private RoomService roomService;

   @Inject
   private GuestService guestService;

   @Inject
   private ServiceManager serviceManager;

   @Inject
   private ImportExportController importExportController;

   private Navigator navigator;

   public MenuController() {}

   public void run() {
      builder.buildConsoleMenu(); 
      navigator = Navigator.getInstance(builder.getRootMenu());
      restoreState(); 
      navigator.start();
      saveState();
   }

   private void saveState() {
      JsonFileService.saveGuests(guestService.getAllGuests());
      System.out.println("Гости сохранены.");

      JsonFileService.saveRooms(roomService.getAllRooms());
      System.out.println("Комнаты сохранены.");

      JsonFileService.saveServices(serviceManager.getAllServices());
      System.out.println("Услуги сохранены.");
   }

   private void restoreState() {
      List<Room> rooms = JsonFileService.loadRooms();
      List<Guest> guests = JsonFileService.loadGuests();
      List<Service> services = JsonFileService.loadServices();

      if (!rooms.isEmpty() || !guests.isEmpty() || !services.isEmpty()) {
         for (Room room : rooms) {
            roomService.addRoom(room);
         }

         for (Guest guest : guests) {
            guestService.checkIn(
                  guest.getName(),
                  guest.getRoom().getNumber(),
                  guest.getCheckInDate(),
                  guest.getCheckOutDate());
         }

         for (Service s : services) {
            serviceManager.addService(s);
         }

         System.out.println("Состояние программы восстановлено.");
      } else {
         System.out.println("Нет сохранённого состояния. Импортируем CSV-файлы...");
         importExportController.importRooms();
         importExportController.importGuests();
         importExportController.importServices();
      }
   }
}
