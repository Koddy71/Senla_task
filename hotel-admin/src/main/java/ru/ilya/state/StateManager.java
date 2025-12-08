package ru.ilya.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Добавляем импорт модуля для Java 8 Date/Time
import java.io.File;
import java.io.IOException;

public class StateManager {
   private static final String STATE_PATH = "src/main/resources/state.json";
   private static final ObjectMapper mapper;

   static {
      // Регистрируем модуль для работы с Java 8 Date/Time API
      mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule()); // Регистрируем модуль для LocalDate и других типов
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
   }

   public static void save(ProgramState state) {
      try {
         File file = new File(STATE_PATH);

         // Если файл не существует, создаем директорию и сам файл
         File parent = file.getParentFile();
         if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
               throw new IOException("Не удалось создать директорию: " + parent.getPath());
            }
         }

         if (!file.exists()) {
            if (!file.createNewFile()) {
               throw new IOException("Не удалось создать файл: " + STATE_PATH);
            }
         }

         // Сохраняем состояние в JSON
         mapper.writeValue(file, state);

      } catch (IOException e) {
         throw new RuntimeException("Невозможно сохранить состояние программы", e);
      }
   }

   public static ProgramState load() {
      try {
         File file = new File(STATE_PATH);

         // Проверяем, существует ли файл и не пуст ли он
         if (!file.exists() || file.length() == 0) {
            System.out.println("Файл состояния пуст или не существует.");
            return null;
         }

         // Читаем файл в строку и печатаем его содержимое для отладки
         String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
         System.out.println("Содержимое файла состояния:\n" + fileContent);

         // Пытаемся десериализовать состояние
         return mapper.readValue(file, ProgramState.class);

      } catch (MismatchedInputException e) {
         System.out.println("Файл состояния пуст или повреждён. Будет использовано начальное состояние.");
         return null;
      } catch (IOException e) {
         throw new RuntimeException("Невозможно загрузить состояние программы", e);
      }
   }

}
