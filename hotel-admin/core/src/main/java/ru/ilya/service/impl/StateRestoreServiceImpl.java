package ru.ilya.service.impl;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.model.*;
import ru.ilya.service.*;
import ru.ilya.controller.ImportExportController;
import ru.ilya.controller.JsonFileController;
import ru.ilya.autodi.Inject;

import java.util.List;

public class StateRestoreServiceImpl implements StateRestoreService {

   @Inject
   private AppConfig config;

   @Inject
   private RoomService roomService;

   @Inject
   private GuestService guestService;

   @Inject
   private ServiceManager serviceManager;

   @Inject
   private ImportExportController importExportController;

   @Override
   public void restore() {
      if ("json".equalsIgnoreCase(config.getStorageType())) {
         restoreFromJson();
      } else if ("csv".equalsIgnoreCase(config.getStorageType())) {
         restoreFromCsv();
      } else {
         throw new RuntimeException(
               "Неизвестный тип хранилища: " + config.getStorageType());
      }
   }

   private void restoreFromJson() {
      List<Room> rooms = JsonFileController.loadRooms();
      List<Guest> guests = JsonFileController.loadGuests();
      List<Service> services = JsonFileController.loadServices();

      if (rooms.isEmpty() && guests.isEmpty() && services.isEmpty()) {
         System.out.println("JSON пуст. Нечего восстанавливать.");
         return;
      }

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

      System.out.println("Состояние восстановлено из JSON.");
   }

   private void restoreFromCsv() {
      System.out.println("Импорт данных из CSV...");
      importExportController.importRooms();
      importExportController.importGuests();
      importExportController.importServices();
   }

   @Override
   public void saveFromJson() {
      JsonFileController.saveGuests(guestService.getAllGuests());
      System.out.println("Гости сохранены.");

      JsonFileController.saveRooms(roomService.getAllRooms());
      System.out.println("Комнаты сохранены.");

      JsonFileController.saveServices(serviceManager.getAllServices());
      System.out.println("Услуги сохранены.");
   }
}
