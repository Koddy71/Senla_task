package ru.ilya.controller;

import java.io.IOException;
import java.io.File;

import ru.ilya.io.importer.GuestImporter;
import ru.ilya.io.importer.RoomImporter;
import ru.ilya.io.importer.ServiceImporter;
import ru.ilya.io.exporter.GuestExporter;
import ru.ilya.io.exporter.RoomExporter;
import ru.ilya.io.exporter.ServiceExporter;

public class ImportExportController {
   private static final String GUESTS_FILE = "data/imports/guests.csv";
   private static final String ROOMS_FILE = "data/imports/rooms.csv";
   private static final String SERVICES_FILE = "data/imports/services.csv";

   private static final String GUESTS_EXPORT_FILE = "data/exports/guests_export.csv";
   private static final String ROOMS_EXPORT_FILE = "data/exports/rooms_export.csv";
   private static final String SERVICES_EXPORT_FILE = "data/exports/services_export.csv";

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
         int imported = guestImporter.importCsv(GUESTS_FILE);
         if (imported==0){
            System.out.println("Импорт завершён. Не удалось добавить ни одного гостя.");
         }else{
            System.out.println("Гости успешно импортированы. Добавлено: " + imported);
         }
      } catch (IOException e) {
         System.out.println("Ошибка при импорте гостей: " + e.getMessage());
      }
   }

   public void importRooms() {
      try {
         int imported = roomImporter.importCsv(ROOMS_FILE);
         if (imported == 0) {
            System.out.println("Импорт комнат завершён. Не удалось добавить ни одной комнаты.");
         } else {
            System.out.println("Комнаты успешно импортированы. Добавлено: " + imported);
         }
      } catch (IOException e) {
         System.out.println("Ошибка при импорте комнат: " + e.getMessage());
      }
   }

   public void importServices() {
      try {
         int imported = serviceImporter.importCsv(SERVICES_FILE);
         if (imported == 0) {
            System.out.println("Импорт услуг завершён. Не удалось добавить ни одной услуги.");
         } else {
            System.out.println("Услуги успешно импортированы. Добавлено: " + imported);
         }
      } catch (IOException e) {
         System.out.println("Ошибка при импорте услуг: " + e.getMessage());
      }
   }

   private void ensureFileExists(String path) throws IOException{
      File file = new File(path);
      if(!file.exists()){
         if(file.createNewFile()){
            System.out.println("Создан новый файл: " + path);
         }
      }
   }
   public void exportGuests() {
      try {
         ensureFileExists(GUESTS_EXPORT_FILE);
         guestExporter.exportCsv(GUESTS_EXPORT_FILE);
         System.out.println("Гости успешно экспортированы.");
      } catch (IOException e) {
         System.out.println("Ошибка при экспорте гостей: " + e.getMessage());
      }
   }

   public void exportRooms() {
      try {
         ensureFileExists(ROOMS_EXPORT_FILE);
         roomExporter.exportCsv(ROOMS_EXPORT_FILE);
         System.out.println("Комнаты успешно экспортированы.");
      } catch (IOException e) {
         System.out.println("Ошибка при экспорте комнат: " + e.getMessage());
      }
   }

   public void exportServices() {
      try {
         ensureFileExists(SERVICES_EXPORT_FILE);
         serviceExporter.exportCsv(SERVICES_EXPORT_FILE);
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
