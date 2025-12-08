package ru.ilya.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.File;
import java.io.IOException;

public class StateManager {
   private static final String STATE_PATH = "src/main/resources/state.json";  
   private static final ObjectMapper mapper = new ObjectMapper();

   public static void save(ProgramState state){
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

         // Если файл пустой или отсутствует, возвращаем null
         if (!file.exists() || file.length() == 0) {
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
