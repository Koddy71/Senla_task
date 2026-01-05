package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.autodi.Inject;
import ru.ilya.io.CsvUtil;
import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

public class GuestExporter {
   @Inject
   private GuestService guestService;

   public GuestExporter(){}

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
