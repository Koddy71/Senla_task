package ru.ilya.controller;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class RoomController {

   private static RoomController instance;
   private final RoomService roomService;
   private final Scanner sc = new Scanner(System.in);

   private RoomController(RoomService roomService) {
      this.roomService = roomService;
   }

   private Integer safeInt(){
      try{
         return Integer.parseInt(sc.nextLine());
      } catch(NumberFormatException e) {
         System.out.println("Введите корректное число.");
         return null;
      }
   }

   public void addRoom() {
      System.out.print("Введите номер комнаты: ");
      Integer number = safeInt();
      if (number == null)
         return;

      System.out.print("Введите цену: ");
      Integer price = safeInt();
      if (price == null)
         return;

      System.out.print("Введите вместимость: ");
      Integer capacity = safeInt();
      if (capacity == null)
         return;

      System.out.print("Введите количество звёзд: ");
      Integer stars = safeInt();
      if (stars == null)
         return;

      Room room = new Room(number, price, capacity, stars);
      boolean ok = roomService.addRoom(room);
      System.out.println(ok ? "Комната добавлена!" : "Ошибка добавления!");
   }

   public void removeRoom() {
      System.out.print("Введите номер комнаты: ");
      Integer number = safeInt();
      if (number == null)
         return;

      boolean ok = roomService.removeRoom(number);
      System.out.println(ok ? "Комната удалена!" : "Комната не найдена!");
   }

   public void showAllRooms() {
      List<Room> rooms = roomService.getAllRooms();
      if (rooms.isEmpty()) {
         System.out.println("Нет комнат.");
         return;
      }
      for (Room r : rooms) {
         System.out.println(r.getInfo());
      }
   }

   public void findRoomByNumber() {
      System.out.print("Введите номер комнаты: ");
      Integer number = safeInt();
      if (number == null)
         return;

      Room r = roomService.findRoom(number);
      System.out.println(r != null ? r.getInfo() : "Комната не найдена.");
   }

   public void changeRoomStatus() {
      System.out.print("Введите номер комнаты: ");
      Integer number = safeInt();
      if (number == null)
         return;

      System.out.print("Введите статус (AVAILABLE / OCCUPIED / MAINTENANCE / RESERVED): ");
      String s = sc.nextLine().trim().toUpperCase();

      try {
         RoomStatus status = RoomStatus.valueOf(s);
         boolean ok = roomService.changeStatus(number, status);
         System.out.println(ok ? "Статус изменён!" : "Комната не найдена.");
      } catch (IllegalArgumentException e) {
         System.out.println("Неверный статус.");
      }
   }

   public void getRoomsFreeByDate() {
      System.out.print("Введите дату (гггг-мм-дд): ");
      String input = sc.nextLine();

      LocalDate date;
      try {
         date = LocalDate.parse(input);
      } catch (DateTimeParseException e) {
         System.out.println("Неверный формат даты.");
         return;
      }

      List<Room> rooms = roomService.getRoomsFreeByDate(date);
      if (rooms.isEmpty()) {
         System.out.println("Нет доступных комнат на эту дату.");
      } else {
         System.out.println("Доступные комнаты:");
         for (Room r : rooms) {
            System.out.println(r.getInfo());
         }
      }
   }

   public void sortRooms() {
      System.out.print("Сортировать по ('price', 'capacity', 'stars'): ");
      String sortBy = sc.nextLine(); 

      List<Room> sorted = roomService.getRoomsSorted(sortBy);
      for (Room r : sorted) {
         System.out.println(r.getInfo());
      }
   }

   public static RoomController getInstance(RoomService roomService) {
      if (instance == null) {
         instance = new RoomController(roomService);
      }
      return instance;
   }
}
