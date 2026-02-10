package ru.ilya.autoconfig;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseManager {
   private static DatabaseManager instance;
   private static final String CONFIG_PATH = "core/src/main/resources/config.properties";
   private static final Properties props = new Properties();

   private final ThreadLocal<Connection> transactionalConnection = new ThreadLocal<>();
   private final ThreadLocal<Integer> transactionDepth = ThreadLocal.withInitial(() -> 0);
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
      Connection txConnection = transactionalConnection.get();
      if (txConnection != null){
         return (Connection) Proxy.newProxyInstance(     //Прокси, который не закрывает соединение
            Connection.class.getClassLoader(), 
            new Class[]{Connection.class}, 
            (proxy, method, args)->{
               if("close".equals(method.getName())){
                  return null;
               }
               return method.invoke(txConnection, args);
            });
      }

      return DriverManager.getConnection(
               props.getProperty("db.url"),
               props.getProperty("db.user"),
               props.getProperty("db.password"));
   }

   public void beginTransaction() throws SQLException{
      if (transactionDepth.get()==0){
         Connection connection = DriverManager.getConnection( 
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password"));
         connection.setAutoCommit(false);
         transactionalConnection.set(connection);
      }
      transactionDepth.set(transactionDepth.get()+1);
   }

   public void commitTransaction() throws SQLException {
      int depth = transactionDepth.get()-1;
      transactionDepth.set(depth);

      if(depth == 0){
         Connection connection = transactionalConnection.get();
         if(connection!=null){
            try{
               connection.commit();
            } finally {
               connection.close();
               transactionalConnection.remove();
            }
         }
      }
   }

   public void rollbackTransaction(){
      try{
         Connection connection = transactionalConnection.get();
         if (connection != null){
            connection.rollback();
            connection.close();
         }
      } catch (SQLException e){
         throw new RuntimeException("Ошибка rollback транзакции", e);
      } finally {
         transactionalConnection.remove();
         transactionDepth.remove();
      }
   }
}
