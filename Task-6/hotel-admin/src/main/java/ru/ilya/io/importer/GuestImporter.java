package ru.ilya.io.importer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

public class GuestImporter {
   private static GuestImporter instance;

   private final GuestService guestService;

   public GuestImporter(GuestService guestService) {
      this.guestService = guestService;
   }

   public void importCsv(String path) throws IOException {
      List<String[]> rows = CsvUtil.read(path);

      for (String[] r : rows) {
         int id = Integer.parseInt(r[0]);
         String name = r[1];
         int roomId = Integer.parseInt(r[2]);
         LocalDate checkInDate = LocalDate.parse(r[3]);
         LocalDate checkOutDate = LocalDate.parse(r[4]);

         Guest existing = guestService.findGuestById(id);
         if (existing == null) {
            guestService.checkIn(name, roomId, checkInDate, checkOutDate);
         } 
      }
   }

   public static GuestImporter getInstance(GuestService guestService) {
      if (instance == null) {
         instance = new GuestImporter(guestService);
      }
      return instance;
   }

}
