package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.autodi.Inject;
import ru.ilya.io.CsvUtil;
import ru.ilya.model.Room;
import ru.ilya.service.RoomService;

public class RoomExporter {
   @Inject
   private RoomService roomService;

   public RoomExporter(){}
   
   public void exportCsv(String path) throws IOException {
      List<String> lines = new ArrayList<>();

      lines.add("number,price,capacity,stars");

      for (Room r : roomService.getAllRooms()) {
         lines.add(
               r.getNumber() + "," +
               r.getPrice() + "," +
               r.getCapacity() + "," +
               r.getStars());
      }

      CsvUtil.write(path, lines);
   }
}
