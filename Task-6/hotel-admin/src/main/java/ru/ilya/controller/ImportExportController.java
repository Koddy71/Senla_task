package ru.ilya.controller;

import java.io.IOException;

import ru.ilya.io.importer.GuestImporter;
import ru.ilya.io.importer.RoomImporter;
import ru.ilya.io.importer.ServiceImporter;
import ru.ilya.io.exporter.GuestExporter;
import ru.ilya.io.exporter.RoomExporter;
import ru.ilya.io.exporter.ServiceExporter;

public class ImportExportController {
   private static ImportExportController instance;
   private final GuestImporter guestImporter;
   private final RoomImporter roomImporter;
   private final ServiceImporter serviceImporter;

   private final GuestExporter guestExporter;
   private final RoomExporter roomExporter;
   private final ServiceExporter serviceExporter;

   public ImportExportController(
         GuestImporter guestImporter,
         RoomImporter roomImporter,
         ServiceImporter serviceImporter,
         GuestExporter guestExporter,
         RoomExporter roomExporter,
         ServiceExporter serviceExporter) {
      this.guestImporter = guestImporter;
      this.roomImporter = roomImporter;
      this.serviceImporter = serviceImporter;

      this.guestExporter = guestExporter;
      this.roomExporter = roomExporter;
      this.serviceExporter = serviceExporter;
   }

   public void importGuests() {
      try {
         guestImporter.importCsv("guests.csv");
         System.out.println("Гости успешно импортированы.");
      } catch (IOException e) {
         System.out.println("Ошибка при импорте гостей: " + e.getMessage());
      }
   }

   public void importRooms() {
      try {
         roomImporter.importCsv("rooms.csv");
         System.out.println("Комнаты успешно импортированы.");
      } catch (IOException e) {
         System.out.println("Ошибка при импорте комнат: " + e.getMessage());
      }
   }

   public void importServices() {
      try {
         serviceImporter.importCsv("services.csv");
         System.out.println("Услуги успешно импортированы.");
      } catch (IOException e) {
         System.out.println("Ошибка при импорте услуг: " + e.getMessage());
      }
   }

   public void exportGuests() {
      try {
         guestExporter.exportCsv("guests_export.csv");
         System.out.println("Гости успешно экспортированы.");
      } catch (IOException e) {
         System.out.println("Ошибка при экспорте гостей: " + e.getMessage());
      }
   }

   public void exportRooms() {
      try {
         roomExporter.exportCsv("rooms_export.csv");
         System.out.println("Комнаты успешно экспортированы.");
      } catch (IOException e) {
         System.out.println("Ошибка при экспорте комнат: " + e.getMessage());
      }
   }

   public void exportServices() {
      try {
         serviceExporter.exportCsv("services_export.csv");
         System.out.println("Услуги успешно экспортированы.");
      } catch (IOException e) {
         System.out.println("Ошибка при экспорте услуг: " + e.getMessage());
      }
   }

   public static ImportExportController getInstance(GuestImporter guestImporter,
         RoomImporter roomImporter,
         ServiceImporter serviceImporter,
         GuestExporter guestExporter,
         RoomExporter roomExporter,
         ServiceExporter serviceExporter) {
   if (instance == null) {
      instance = new ImportExportController(guestImporter,roomImporter,serviceImporter,guestExporter,roomExporter,serviceExporter);
   }
   return instance;
   }
}
