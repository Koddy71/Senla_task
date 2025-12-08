package ru.ilya;

import ru.ilya.controller.*;
import ru.ilya.service.GuestService;
import ru.ilya.service.PriceService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.service.impl.GuestServiceImpl;
import ru.ilya.service.impl.PriceServiceImpl;
import ru.ilya.service.impl.RoomServiceImpl;
import ru.ilya.service.impl.ServiceManagerImpl;
import ru.ilya.state.ProgramState;
import ru.ilya.state.StateManager;
import ru.ilya.ui.Builder;
import ru.ilya.ui.MenuBuilder;
import ru.ilya.ui.MenuController;
import ru.ilya.io.importer.*;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.io.exporter.*;

public class HotelAdmin {
   public static void main(String[] args) {
      RoomService roomService = RoomServiceImpl.getInstance();
      ServiceManager serviceManager = ServiceManagerImpl.getInstance();
      GuestService guestService = GuestServiceImpl.getInstance(roomService, serviceManager);
      PriceService priceService = PriceServiceImpl.getInstance(roomService, serviceManager);

      GuestImporter guestImporter = GuestImporter.getInstance(guestService);
      RoomImporter roomImporter = RoomImporter.getInstance(roomService);
      ServiceImporter serviceImporter = ServiceImporter.getInstance(serviceManager);

      GuestExporter guestExporter = GuestExporter.getInstance(guestService);
      RoomExporter roomExporter = RoomExporter.getInstance(roomService);
      ServiceExporter serviceExporter = ServiceExporter.getInstance(serviceManager);

      GuestController guestController = GuestController.getInstance(guestService);
      RoomController roomController = RoomController.getInstance(roomService);
      ServiceController serviceController = ServiceController.getInstance(serviceManager);
      PriceController priceController = PriceController.getInstance(priceService);
      ImportExportController importExportController = ImportExportController.getInstance(guestImporter, roomImporter,
            serviceImporter, guestExporter, roomExporter, serviceExporter);

      ProgramState state = StateManager.load();
      if (state != null) {
         for (Room room : state.getRooms()) {
            roomService.addRoom(room);
         }

         for (Guest guest : state.getGuests()) {
            guestService.checkIn(
                     guest.getName(),
                     guest.getRoom().getNumber(),
                     guest.getCheckInDate(),
                     guest.getCheckOutDate()
            );
         }

         for (Service s : state.getServices()) {
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