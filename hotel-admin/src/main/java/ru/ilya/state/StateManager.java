package ru.ilya.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;

public class StateManager {
   private static final String STATE_PATH = "src/main/resources/state.json";
   private static final ObjectMapper mapper;

   static {
      mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule()); // модуль для LocalDate
      mapper.enable(SerializationFeature.INDENT_OUTPUT); //для красивого форматирования
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

         mapper.writeValue(file, state);

      } catch (IOException e) {
         throw new RuntimeException("Невозможно сохранить состояние программы", e);
      }
   }

   public static ProgramState load() {
      try {
         File file = new File(STATE_PATH);

         if (!file.exists() || file.length() == 0) {
            System.out.println("Файл состояния пуст или не существует.");
            return null;
         }

         return mapper.readValue(file, ProgramState.class);

      } catch (MismatchedInputException e) {
         System.out.println("Файл состояния пуст или повреждён. Будет использовано начальное состояние.");
         return null;
      } catch (IOException e) {
         throw new RuntimeException("Невозможно загрузить состояние программы", e);
      }
   }

}
