package ru.ilya;

import ru.ilya.controller.GuestController;
import ru.ilya.controller.ImportExportController;
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
import ru.ilya.io.importer.GuestImporter;
import ru.ilya.io.importer.RoomImporter;
import ru.ilya.io.importer.ServiceImporter;
import ru.ilya.io.exporter.GuestExporter;
import ru.ilya.io.exporter.RoomExporter;
import ru.ilya.io.exporter.ServiceExporter;

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

      importExportController.importRooms(); 
      importExportController.importServices(); 
      importExportController.importGuests();

      try{
         MenuBuilder factory = new MenuBuilder(guestController, roomController, serviceController, priceController, importExportController);

         Builder builder = new Builder(factory);

         MenuController menuController = new MenuController(builder);
         menuController.run();
      } catch (Exception e) {
         System.out.println("Произошла критическая ошибка.");
         System.out.println("Причина: " + e.getMessage());
      }
   }
}