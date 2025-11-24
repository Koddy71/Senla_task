package ru.ilya.io.importer;

import java.io.IOException;
import java.util.List;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;

public class RoomImporter {
   private static RoomImporter instance;

   private final RoomService roomService;

   public RoomImporter(RoomService roomService) {
      this.roomService = roomService;
   }

   public static RoomImporter getInstance(RoomService roomService) {
      if (instance == null) {
         instance = new RoomImporter(roomService);
      }
      return instance;
   }
   
   public void importCsv(String path) throws IOException {
      List<String[]> rows = CsvUtil.read(path);

      for (String[] r : rows) {
         int id = Integer.parseInt(r[0]);
         int number = Integer.parseInt(r[1]);
         RoomStatus status = RoomStatus.valueOf(r[2]);
         int price = Integer.parseInt(r[3]);
         int capacity = Integer.parseInt(r[4]);
         int stars = Integer.parseInt(r[5]);

         Room room = new Room(id, number, price, capacity, stars, status);

         roomService.addRoom(room);
      }
   }
}
