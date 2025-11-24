package ru.ilya.service.impl;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;
import java.time.LocalDate;

import java.util.*;

public class RoomServiceImpl implements RoomService {

   private Map<Integer, Room> rooms = new HashMap<>();
   private static RoomServiceImpl instance;

   private RoomServiceImpl() {
   }

   @Override
   public boolean addRoom(Room room) {
      if (room == null || rooms.containsKey(room.getId())) {
         return false;
      }
      rooms.put(room.getId(), room);
      return true;
   }

   @Override
   public boolean removeRoom(int id) {
      return rooms.remove(id) != null;
   }

   @Override
   public Room findRoom(int id) {
      return rooms.get(id);
   }

   @Override
   public boolean checkIn(int id) {
      Room room = findRoom(id);
      if (room != null && room.getStatus() == RoomStatus.AVAILABLE) {
         room.setStatus(RoomStatus.OCCUPIED);
         return true;
      }
      return false;
   }

   @Override
   public boolean checkOut(int id) {
      Room room = findRoom(id);
      if (room != null && room.getStatus() == RoomStatus.OCCUPIED) {
         room.setStatus(RoomStatus.AVAILABLE);
         return true;
      }
      return false;
   }

   @Override
   public boolean changeStatus(int id, RoomStatus status) {
      Room room = findRoom(id);
      if (room != null) {
         room.setStatus(status);
         return true;
      }
      return false;
   }

   public boolean changePrice(int id, int newPrice) {
      Room room = findRoom(id);
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
      for (Room r : rooms.values()) {
         if (r.getStatus() == RoomStatus.AVAILABLE) {
            freeRooms.add(r);
         }
      }
      return freeRooms;
   }

   @Override
   public int countFreeRooms() {
      int count = 0;
      for (Room r : rooms.values()) {
         if (r.getStatus() == RoomStatus.AVAILABLE) {
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

   public static RoomServiceImpl getInstance() {
      if (instance == null) {
         instance = new RoomServiceImpl();
      }
      return instance;
   }
}
