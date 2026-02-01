package ru.ilya.service.impl;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.autodi.Inject;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;
import java.time.LocalDate;

import java.util.*;

public class RoomServiceImpl implements RoomService {
   private Map<Integer, Room> rooms = new HashMap<>();

   @Inject
   private AppConfig appConfig;

   public RoomServiceImpl(){}

   @Override
   public boolean addRoom(Room room) {
      if (room == null || rooms.containsKey(room.getNumber())) {
         return false;
      }
      rooms.put(room.getNumber(), room);
      return true;
   }

   @Override
   public boolean removeRoom(int number) {
      return rooms.remove(number) != null;
   }

   @Override
   public Room findRoom(int number) {
      return rooms.get(number);
   }

   @Override
   public boolean checkIn(int number) {
      Room room = findRoom(number);
      if (room != null && room.isFreeOn(LocalDate.now())) {
         return true;
      }
      return false;
   }

   @Override
   public boolean checkOut(int number) {
      Room room = findRoom(number);
      if (room != null) {
         return true;
      }
      return false;
   }

   @Override
   public boolean changeStatus(int number, RoomStatus status) {
      if (!appConfig.isRoomStatusChangeEnable()){
         System.out.println("Изменение статуса номеров отключено в настройках.");
         return false;
      }
      
      Room room = findRoom(number);
      if (room != null) {
         room.setStatus(status);
         return true;
      }
      return false;
   }

   public boolean changePrice(int number, int newPrice) {
      Room room = findRoom(number);
      if (room != null && newPrice > 0) {
         room.setPrice(newPrice);
         return true;
      }
      return false;
   }

   @Override
   public List<Room> getAllRooms() {
      return new ArrayList<>(rooms.values());
   }

   @Override
   public List<Room> getFreeRooms() {
      List<Room> freeRooms = new ArrayList<>();
      LocalDate today = LocalDate.now();
      for (Room r : rooms.values()) {
         if (r.isFreeOn(today)) {
            freeRooms.add(r);
         }
      }
      return freeRooms;
   }

   @Override
   public int countFreeRooms() {
      int count = 0;
      LocalDate today = LocalDate.now();
      for (Room r : rooms.values()) {
         if (r.isFreeOn(today)) {
            count++;
         }
      }
      return count;
   }

   @Override
   public List<Room> getRoomsFreeByDate(LocalDate date) {
      List<Room> freeByDate = new ArrayList<>();
      for (Room r : rooms.values()) {
         if (r.isFreeOn(date)) {
            freeByDate.add(r);
         }
      }
      return freeByDate;
   }

   @Override
   public List<Room> getRoomsSorted(String sortBy) {
      List<Room> sorted = new ArrayList<>(rooms.values());

      if ("price".equalsIgnoreCase(sortBy)) {
         sorted.sort(Comparator.comparingDouble(Room::getPrice).reversed());
      } else if ("capacity".equalsIgnoreCase(sortBy)) {
         sorted.sort(Comparator.comparingInt(Room::getCapacity).reversed());
      } else if ("stars".equalsIgnoreCase(sortBy)) {
         sorted.sort(Comparator.comparingInt(Room::getStars).reversed());
      } else{
         sorted.sort(Comparator.comparingDouble(Room::getPrice).reversed());
      }
      
      return sorted;
   }
}
