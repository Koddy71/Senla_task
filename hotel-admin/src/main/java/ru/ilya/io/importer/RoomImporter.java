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
   
   public int importCsv(String path) throws IOException {
      int count=0;
      List<String[]> rows = CsvUtil.read(path);
      for (String[] r : rows) {
         if (r.length <5){
            System.out.println("Недостаточно данных в строке" + String.join(",",r));
            continue;
         }
         try {
            int number = Integer.parseInt(r[0].trim());
            int price = Integer.parseInt(r[1].trim());
            int capacity = Integer.parseInt(r[2].trim());
            int stars = Integer.parseInt(r[3].trim());
            RoomStatus status = RoomStatus.valueOf(r[4].trim().toUpperCase());

            if (number <= 0 || price < 0 || capacity <= 0 || stars < 0) {
               System.out.println("Ошибка: некорректные значения в строке: " + String.join(",", r));
               continue;
            }

            Room room = new Room(number, price, capacity, stars, status);
            boolean ok = roomService.addRoom(room);
            if (ok) {
               count++; 
            } else {
               System.out.println("Комната не добавлена (возможно, такой номер уже существует): " + String.join(",", r));
            }

         } catch (NumberFormatException e) {
            System.out.println("Ошибка формата данных: " + String.join(",", r));
         } catch (IllegalArgumentException e) {
            System.out.println("Ошибка статуса комнаты: " + String.join(",", r));
         }
      }
      return count;
   }
}
