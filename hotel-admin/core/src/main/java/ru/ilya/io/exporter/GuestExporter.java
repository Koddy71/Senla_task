package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.autodi.Inject;
import ru.ilya.io.CsvUtil;
import ru.ilya.model.Guest;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;

public class GuestExporter {
   @Inject
   private GuestService guestService;

   public GuestExporter() {
   }

   public void exportCsv(String path) throws IOException {
      List<String> lines = new ArrayList<>();

      lines.add("id,name,roomNumber,checkInDate,checkOutDate,serviceID");

      for (Guest g : guestService.getAllGuests()) {
         int roomNumber = g.getRoom().getNumber();

         StringBuilder serviceIDs = new StringBuilder();
         List<Service> services = g.getServices();

         for (int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            serviceIDs.append(service.getId());
            if (i < services.size() - 1) {
               serviceIDs.append("|");
            }
         }

         lines.add(
               g.getId() + "," +
                     g.getName() + "," +
                     roomNumber + "," +
                     g.getCheckInDate() + "," +
                     g.getCheckOutDate() + "," +
                     serviceIDs);
      }

      CsvUtil.write(path, lines);
   }
}
