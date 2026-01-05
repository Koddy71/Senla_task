package ru.ilya;

import java.util.List;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.autoconfig.ConfigInjector;
import ru.ilya.autodi.DIInjector;
import ru.ilya.controller.*;
import ru.ilya.service.*;
import ru.ilya.ui.Builder;
import ru.ilya.ui.MenuBuilder;
import ru.ilya.ui.MenuController;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;

public class HotelAdmin {
   public static void main(String[] args) {
      String configPath = "src/main/resources/config.properties";
      ConfigInjector injector = new ConfigInjector(configPath);
      AppConfig appConfig = new AppConfig();
      injector.configure(appConfig);

      DIInjector di = new DIInjector();
      di.inject(appConfig);

      RoomService roomService = di.getInstance(RoomService.class);
      GuestService guestService = di.getInstance(GuestService.class);
      ServiceManager serviceManager = di.getInstance(ServiceManager.class);

      GuestController guestController = di.getInstance(GuestController.class);
      RoomController roomController = di.getInstance(RoomController.class);
      ServiceController serviceController = di.getInstance(ServiceController.class);
      PriceController priceController = di.getInstance(PriceController.class);
      ImportExportController importExportController = di.getInstance(ImportExportController.class);

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
                     guest.getCheckOutDate()
            );
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

      try{
         MenuBuilder factory = new MenuBuilder(guestController, roomController, serviceController, priceController, importExportController);

         Builder builder = new Builder(factory);

         MenuController menuController = new MenuController(builder, roomService, guestService, serviceManager);
         menuController.run();
      } catch (Exception e) {
         System.out.println("Произошла критическая ошибка.");
         System.out.println("Причина: " + e.getMessage());
      }
   }
}