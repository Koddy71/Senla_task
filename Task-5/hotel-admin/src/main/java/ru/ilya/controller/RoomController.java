package ru.ilya.controller;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class RoomController {
   private static RoomController instance;
   private RoomService roomService;
   private Scanner sc = new Scanner(System.in);

   private RoomController(RoomService roomService) {
      this.roomService = roomService;
   }

   public void addRoom() {
      System.out.print("Введите номер комнаты: ");
      int number = sc.nextInt();
      System.out.print("Введите цену: ");
      int price = sc.nextInt();
      System.out.print("Введите вместимость: ");
      int capacity = sc.nextInt();
      System.out.print("Введите количество звёзд: ");
      int stars = sc.nextInt();
      sc.nextLine();

      Room room = new Room(number, price, capacity, stars);
      boolean ok = roomService.addRoom(room);
      System.out.println(ok ? "Комната добавлена!" : "Ошибка добавления!");
   }

   public void removeRoom() {
      System.out.print("Введите номер комнаты: ");
      int number = sc.nextInt();
      sc.nextLine();

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
      int number = sc.nextInt();
      sc.nextLine();

      Room r = roomService.findRoom(number);
      if (r != null) {
         System.out.println(r.getInfo());
      } else {
         System.out.println("Комната не найдена.");
      }
   }

   public void changeRoomStatus() {
      System.out.print("Введите номер комнаты: ");
      int number = sc.nextInt();
      sc.nextLine();

      System.out.print("Введите статус (AVAILABLE / OCCUPIED / MAINTENANCE / RESERVED): ");
      String s = sc.nextLine().toUpperCase();

      RoomStatus status = RoomStatus.valueOf(s);
      roomService.changeStatus(number, status);
      System.out.println("Статус изменён!");
   }

   public void getRoomsFreeByDate() {     
      System.out.print("Введите дату (гггг-мм-дд): ");
      LocalDate date = LocalDate.parse(sc.nextLine());

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
