package ru.ilya.autoconfig;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseManager {
   private static DatabaseManager instance;
   private static final String CONFIG_PATH = "core/src/main/resources/config.properties";
   private static final Properties props = new Properties();

   static {
      try(FileReader reader = new FileReader(CONFIG_PATH)){
         props.load(reader);
      } catch (IOException e){
         throw new RuntimeException("Не удалось загрузить файл конфигурации: " + CONFIG_PATH, e);
      }
   }

   private DatabaseManager(){
      try{
         Class.forName(props.getProperty("db.driver"));
      } catch (ClassNotFoundException e) {
         throw new RuntimeException("JDBC Driver не найден", e);
      }
   }

   public static DatabaseManager getInstance() {
      if (instance == null) {
         instance = new DatabaseManager();
      }
      return instance;
   }

   public Connection getConnection() throws SQLException {
      return DriverManager.getConnection(
               props.getProperty("db.url"),
               props.getProperty("db.user"),
               props.getProperty("db.password"));
    }
}
