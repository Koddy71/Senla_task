package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Room;
import ru.ilya.service.RoomService;

public class RoomExporter {
   private static RoomExporter instance;

   private final RoomService roomService;

   private RoomExporter(RoomService roomService) {
      this.roomService = roomService;
   }

   public static RoomExporter getInstance(RoomService roomService) {
      if (instance == null) {
         instance = new RoomExporter(roomService);
      }
      return instance;
   }
   
   public void exportCsv(String path) throws IOException {
      List<String> lines = new ArrayList<>();

      lines.add("number,price,capacity,stars,status");

      for (Room r : roomService.getAllRooms()) {
         lines.add(
               r.getNumber() + "," +
               r.getPrice() + "," +
               r.getCapacity() + "," +
               r.getStars() + "," +
               r.getStatus());
      }

      CsvUtil.write(path, lines);
   }
}
