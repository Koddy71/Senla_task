package ru.ilya.io.importer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import ru.ilya.autodi.Inject;
import ru.ilya.io.CsvUtil;
import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

public class GuestImporter {
   @Inject
   private GuestService guestService;

   public GuestImporter(){}

   public int importCsv(String path) throws IOException {
      int count=0;
      List<String[]> rows = CsvUtil.read(path);
      for (String[] r : rows) {
         if (r.length < 5) {
            System.out.println("Ошибка: недостаточно данных в строке: " + String.join(",", r));
            continue;
         }

         try {
            int id = Integer.parseInt(r[0].trim());
            String name = r[1].trim();
            int roomId = Integer.parseInt(r[2].trim());
            int serviceId = Integer.parseInt(r[3].trim());
            LocalDate checkInDate = LocalDate.parse(r[4].trim());
            LocalDate checkOutDate = LocalDate.parse(r[5].trim());

            Guest existing = guestService.findGuestById(id);
            if (existing == null) {
               Guest g = guestService.checkIn(name, roomId, checkInDate, checkOutDate);
               if (g!=null){
                  count++;
               } else{
                  System.out.println("Не удалось заселить гостя: " + String.join(",", r));
               }
               
            }

         } catch (NumberFormatException e) {
            System.out.println("Ошибка формата числовых данных: " + String.join(",", r));
         } catch (DateTimeParseException e) {
            System.out.println("Ошибка формата даты: " + String.join(",", r));
         } catch (Exception e) {
            System.out.println("Ошибка при регистрации гостя: " + e.getMessage());
         }
      }
      return count;
   }
}
