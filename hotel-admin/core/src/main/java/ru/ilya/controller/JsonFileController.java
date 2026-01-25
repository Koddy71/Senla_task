package ru.ilya.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonFileController {
   private static final String GUESTS_FILE_PATH = "core/src/main/resources/guests.json";
   private static final String ROOMS_FILE_PATH = "core/src/main/resources/rooms.json";
   private static final String SERVICES_FILE_PATH = "core/src/main/resources/services.json";
   private static final ObjectMapper mapper;

   static {
      mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      mapper.enable(SerializationFeature.INDENT_OUTPUT); 
   }

   private static void createFileIfNeeded(File file) throws IOException {
      File parent = file.getParentFile();
      if (parent != null && !parent.exists()) {
         if (!parent.mkdirs()) {
            throw new IOException("Не удалось создать директорию: " + parent.getPath());
         }
      }
      if (!file.exists()) {
         if (!file.createNewFile()) {
            throw new IOException("Не удалось создать файл: " + file.getPath());
         }
      }
   }

   public static void saveGuests(List<Guest> guests) {
      try {
         File file = new File(GUESTS_FILE_PATH);
         createFileIfNeeded(file);
         mapper.writeValue(file, guests);
      } catch (IOException e) {
         throw new RuntimeException("Ошибка при сохранении данных гостей.", e);
      }
   }

   public static void saveRooms(List<Room> rooms) {
      try {
         File file = new File(ROOMS_FILE_PATH);
         createFileIfNeeded(file);
         mapper.writeValue(file, rooms);
      } catch (IOException e) {
         throw new RuntimeException("Ошибка при сохранении данных комнат.", e);
      }
   }

   public static void saveServices(List<Service> services) {
      try {
         File file = new File(SERVICES_FILE_PATH);
         createFileIfNeeded(file);
         mapper.writeValue(file, services);
      } catch (IOException e) {
         throw new RuntimeException("Ошибка при сохранении данных услуг.", e);
      }
   }

   public static List<Guest> loadGuests() {
      try {
         File file = new File(GUESTS_FILE_PATH);
         boolean wasCreated = !file.exists();
         createFileIfNeeded(file);

         if (wasCreated) {
            System.out.println("Файл с данными гостей не существовал, создан новый пустой файл.");
            return new ArrayList<>();
         }

         if (file.length() == 0) {
            System.out.println("Файл с данными гостей существует, но он пуст.");
            return new ArrayList<>();
         }

         return mapper.readValue(file, new TypeReference<List<Guest>>() {});
      } catch (IOException e) {
         throw new RuntimeException("Ошибка при загрузке данных гостей.", e);
      }
   }

   public static List<Room> loadRooms() {
      try {
         File file = new File(ROOMS_FILE_PATH);
         boolean wasCreated = !file.exists();
         createFileIfNeeded(file);

         if (wasCreated) {
            System.out.println("Файл с данными комнат не существовал, создан новый пустой файл.");
            return new ArrayList<>();
         }

         if (file.length() == 0) {
            System.out.println("Файл с данными комнат существует, но ое пуст.");
            return new ArrayList<>();
         }

         return mapper.readValue(file, new TypeReference<List<Room>>() {});
      } catch (IOException e) {
         throw new RuntimeException("Ошибка при загрузке данных комнат.", e);
      }
   }

   public static List<Service> loadServices() {
      try {
         File file = new File(SERVICES_FILE_PATH);
         boolean wasCreated = !file.exists();
         createFileIfNeeded(file);

         if (wasCreated) {
            System.out.println("Файл с данными услуг не существовал, создан новый пустой файл.");
            return new ArrayList<>();
         }

         if (file.length() == 0) {
            System.out.println("Файл с данными услуг существует, но он пуст.");
            return new ArrayList<>();
         }

         return mapper.readValue(file, new TypeReference<List<Service>>() {});
      } catch (IOException e) {
         throw new RuntimeException("Ошибка при загрузке данных услуг.", e);
      }
   }
}
