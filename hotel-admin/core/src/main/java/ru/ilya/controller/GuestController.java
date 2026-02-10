package ru.ilya.controller;

import ru.ilya.autodi.Inject;
import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class GuestController {

   @Inject
   private GuestService guestService;

   private final Scanner sc = new Scanner(System.in);

   public GuestController() {
   }

   private Integer safeInt() {
      try {
         return Integer.parseInt(sc.nextLine());
      } catch (NumberFormatException e) {
         System.out.println("Введите корректное число.");
         return null;
      }
   }

   private LocalDate safeDate() {
      String input = sc.nextLine().trim();
      try {
         return LocalDate.parse(input);
      } catch (DateTimeParseException e) {
         System.out.println("Неверная дата. Формат: гггг-мм-дд");
         return null;
      }
   }

   public void checkInGuest() {
      System.out.print("Введите имя гостя: ");
      String name = sc.nextLine().trim();

      if (name.isEmpty()) {
         System.out.println("Имя не может быть пустым.");
         return;
      }

      System.out.print("Введите ID комнаты: ");
      Integer roomId = safeInt();
      if (roomId == null)
         return;

      System.out.print("Дата заезда (гггг-мм-дд): ");
      LocalDate from = safeDate();
      if (from == null)
         return;

      System.out.print("Дата выезда (гггг-мм-дд): ");
      LocalDate to = safeDate();
      if (to == null)
         return;

      Guest guest = guestService.checkIn(name, roomId, from, to);
      if (guest != null) {
         System.out.println("Гость заселён! ID: " + guest.getId());
      } else {
         System.out.println("Ошибка заселения! Комната занята, не найдена или даты неверны.");
      }
   }

   public void checkOutGuest() {
      System.out.print("Введите ID гостя: ");
      Integer id = safeInt();
      if (id == null)
         return;

      boolean ok = guestService.checkOut(id);
      System.out.println(ok ? "Гость выселен!" : "Ошибка выселения! Гость не найден.");
   }

   public void showAllGuests() {
      List<Guest> guests = guestService.getAllGuests();
      if (guests.isEmpty()) {
         System.out.println("Нет заселённых гостей.");
         return;
      }
      for (Guest g : guests) {
         System.out.println(g.getInfo());
      }
   }

   public void findGuestById() {
      System.out.print("Введите ID гостя: ");
      Integer id = safeInt();
      if (id == null)
         return;

      Guest g = guestService.findGuestById(id);
      System.out.println(g != null ? g.getInfo() : "Гость не найден.");
   }

   public void showGuestCount() {
      System.out.println("Количество гостей: " + guestService.getGuestCount());
   }

   public void sortGuests() {
      System.out.print("Сортировать по ('name' или 'checkoutDate'): ");
      String sortBy = sc.nextLine();
      List<Guest> sorted = guestService.getGuestsSorted(sortBy);

      for (Guest g : sorted) {
         System.out.println(g.getInfo());
      }
   }

   public void addService() {
      System.out.print("Введите ID гостя: ");
      Integer guestId = safeInt();
      if (guestId == null)
         return;

      System.out.print("Введите ID услуги: ");
      Integer serviceId = safeInt();
      if (serviceId == null)
         return;

      boolean success = guestService.addServiceToGuest(guestId, serviceId);
      if (success) {
         System.out.println("Услуга успешно добавлена гостю.");
      } else {
         System.out.println(
               "Не удалось добавить услугу. Возможно, гость или услуга не найдены, либо услуга уже добавлена.");
      }
   }

   public void removeService() {
      System.out.print("Введите ID гостя: ");
      Integer guestId = safeInt();
      if (guestId == null)
         return;

      System.out.print("Введите ID услуги: ");
      Integer serviceId = safeInt();
      if (serviceId == null)
         return;

      boolean success = guestService.removeServiceFromGuest(guestId, serviceId);
      if (success) {
         System.out.println("Услуга успешно удалена у гостя.");
      } else {
         System.out.println("Не удалось удалить услугу. Возможно, гость или услуга не найдены.");
      }
   }
}
