package ru.ilya.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.autoconfig.DatabaseManager;
import ru.ilya.autodi.Inject;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;

public class RoomDao implements GenericDao<Room, Integer> {
   private static final String INSERT_SQL = "INSERT INTO room(number, price, capacity, stars) VALUES (?, ?, ?, ?)";
   private static final String SELECT_BY_ID_SQL = "SELECT number, price, capacity, stars FROM room WHERE number = ?";
   private static final String SELECT_ALL_SQL = "SELECT number, price, capacity, stars FROM room";
   private static final String UPDATE_SQL = "UPDATE room SET price = ?, capacity = ?, stars = ? WHERE number = ?";
   private static final String DELETE_SQL = "DELETE FROM room WHERE number = ?";
   private static final String DELETE_ALL_SQL = "DELETE FROM room";

   private static final String COLUMN_NUMBER = "number";
   private static final String COLUMN_PRICE = "price";
   private static final String COLUMN_CAPACITY = "capacity";
   private static final String COLUMN_STARS = "stars";

   @Inject
   private DatabaseManager dbManager;

   public RoomDao(){
   }
   
   @Override
   public Room create(Room room) {
      try (Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(INSERT_SQL)) {
         statement.setInt(1, room.getNumber());
         statement.setInt(2, room.getPrice());
         statement.setInt(3, room.getCapacity());
         statement.setInt(4, room.getStars());
         statement.executeUpdate();
         return room;
      } catch (SQLException e) {
         throw new RuntimeException("Ошибка вставки комнаты в БД", e);
      }
   }

   @Override
   public Room findById(Integer number) {
      try (Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_ID_SQL)) {
         statement.setInt(1, number);
         try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
               Room room = new Room();
               room.setNumber(rs.getInt(COLUMN_NUMBER));
               room.setPrice(rs.getInt(COLUMN_PRICE));
               room.setCapacity(rs.getInt(COLUMN_CAPACITY));
               room.setStars(rs.getInt(COLUMN_STARS));
               room.setStatus(RoomStatus.AVAILABLE);
               return room;
            }
         }
      } catch (SQLException e) {
         throw new RuntimeException("Ошибка выборки команды из БД", e);
      }
      return null;
   }

   @Override
   public List<Room> findAll() {
      List<Room> list = new ArrayList<>();
      try (Connection conn = dbManager.getConnection();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_SQL)) {
         while (rs.next()) {
            Room room = new Room();
            room.setNumber(rs.getInt(COLUMN_NUMBER));
            room.setPrice(rs.getInt(COLUMN_PRICE));
            room.setCapacity(rs.getInt(COLUMN_CAPACITY));
            room.setStars(rs.getInt(COLUMN_STARS));
            room.setStatus(RoomStatus.AVAILABLE);
            list.add(room);
         }
      } catch (SQLException e) {
         throw new RuntimeException("Ошибка выборки всех комнат из БД", e);
      }
      return list;
   }

   @Override
   public Room update(Room room) {
      try (Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(UPDATE_SQL)) {
         statement.setInt(1, room.getPrice());
         statement.setInt(2, room.getCapacity());
         statement.setInt(3, room.getStars());
         statement.setInt(4, room.getNumber());
         statement.executeUpdate();
         return room;
      } catch (SQLException e) {
         throw new RuntimeException("Ошибка обновления комнаты в БД", e);
      }
   }

   @Override
   public boolean delete(Integer number) {
      try (Connection conn = dbManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(DELETE_SQL)) {
         statement.setInt(1, number);
         int affected = statement.executeUpdate();
         return affected > 0;
      } catch (SQLException e) {
         throw new RuntimeException("Ошибка удаления комнаты из БД", e);
      }
   }

   @Override
   public void deleteAll() {
      try (Connection conn = dbManager.getConnection();
            Statement statement = conn.createStatement()) {
         statement.executeUpdate(DELETE_ALL_SQL);
      } catch (SQLException e) {
         throw new RuntimeException("Ошибка очистки таблицы room", e);
      }
   }
}
