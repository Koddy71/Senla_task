package ru.ilya.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class RoomConfig {
   private static final Properties properties = new Properties();
   private static final String CONFIG_PATH = "core/src/main/resources/config.properties";

   static {
      try(FileReader reader = new FileReader(CONFIG_PATH)){
         properties.load(reader);
      } catch (IOException e){
         throw new RuntimeException("Не удалось загрузить файл конфигурации: " + CONFIG_PATH, e);
      }
   }

   public static boolean isRoomStatusChangeEnable(){
      return Boolean.parseBoolean(properties.getProperty("room.status.change.enable", "true"));
   }

   public static int getHistoryRoomLimit(){
      return Integer.parseInt(properties.getProperty("room.history.limit", "10"));
   }
}
