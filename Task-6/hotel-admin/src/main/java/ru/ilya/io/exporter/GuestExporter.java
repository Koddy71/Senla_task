package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

public class GuestExporter {
   private static GuestExporter instance;
   
   private final GuestService guestService;

   private GuestExporter(GuestService guestService) {
      this.guestService = guestService;
   }

   public static GuestExporter getInstance(GuestService guestService) {
      if (instance == null) {
         instance = new GuestExporter(guestService);
      }
      return instance;
   }

   public void exportCsv(String path) throws IOException {
      List<String> lines = new ArrayList<>();

      lines.add("id,name,roomNumber,checkInDate,checkOutDate");

      for (Guest g : guestService.getAllGuests()) {
         int roomNumber = g.getRoom().getNumber();

         lines.add(
               g.getId() + "," +
               g.getName() + "," +
               roomNumber+ "," +
               g.getCheckInDate() + "," +
               g.getCheckOutDate());
      }

      CsvUtil.write(path, lines);
   }
}
