package ru.ilya.dao.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ilya.autoconfig.JdbcManager;
import ru.ilya.autodi.Inject;
import ru.ilya.dao.GenericDao;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;

import java.util.ArrayList;
import java.util.List;

public class GuestDaoJdbc implements GenericDao<Guest, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(GuestDaoJdbc.class);

    private static final String INSERT_GUEST_SQL = """
            INSERT INTO guest(id, name, roomNumber, checkInDate, checkOutDate)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String INSERT_GUEST_SERVICE_SQL = """
            INSERT INTO guest_service(guest_id, service_id) VALUES (?, ?)
            """;;
    private static final String SELECT_GUEST_BY_ID_SQL = """
            SELECT id, name, roomNumber, checkInDate, checkOutDate FROM guest WHERE id = ?
            """;
    private static final String SELECT_ALL_GUESTS_SQL = """
            SELECT id, name, roomNumber, checkInDate, checkOutDate FROM guest
            """;
    private static final String UPDATE_GUEST_SQL = """
            UPDATE guest SET name = ?, roomNumber = ?, checkInDate = ?, checkOutDate = ? WHERE id = ?
            """;
    private static final String DELETE_GUEST_SERVICES_SQL = "DELETE FROM guest_service WHERE guest_id = ?";
    private static final String DELETE_GUEST_SQL = "DELETE FROM guest WHERE id = ?";
    private static final String SELECT_GUEST_SERVICES_SQL = """
            SELECT s.id, s.name, s.price FROM service s
            JOIN guest_service gs ON s.id = gs.service_id
            WHERE gs.guest_id = ?
            """;
    private static final String DELETE_GUEST_SERVICES_ALL_SQL = "DELETE FROM guest_service";
    private static final String DELETE_GUEST_ALL_SQL = "DELETE FROM guest";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ROOM_NUMBER = "roomNumber";
    private static final String COLUMN_CHECK_IN_DATE = "checkInDate";
    private static final String COLUMN_CHECK_OUT_DATE = "checkOutDate";
    private static final String COLUMN_PRICE = "price";

    @Inject
    private JdbcManager jdbcManager;

    public GuestDaoJdbc() {
    }

    @Override
    public Guest create(Guest guest) {
        try (Connection conn = jdbcManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(INSERT_GUEST_SQL)) {
            statement.setInt(1, guest.getId());
            statement.setString(2, guest.getName());
            statement.setInt(3, guest.getRoom().getNumber());
            statement.setDate(4, Date.valueOf(guest.getCheckInDate()));
            statement.setDate(5, Date.valueOf(guest.getCheckOutDate()));
            statement.executeUpdate();
            for (Service s : guest.getServices()) {
                addServiceToGuest(guest.getId(), s.getId(), conn);
            }
            return guest;
        } catch (SQLException e) {
            logger.error("Ошибка вставки гостя в БД", e);
            throw new RuntimeException("Ошибка вставки гостя в БД", e);
        }
    }

    @Override
    public Guest findById(Integer id) {
        try (Connection conn = jdbcManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(SELECT_GUEST_BY_ID_SQL)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Guest guest = new Guest();
                    guest.setId(rs.getInt(COLUMN_ID));
                    guest.setName(rs.getString(COLUMN_NAME));
                    int roomNumber = rs.getInt(COLUMN_ROOM_NUMBER);
                    Room room = new Room();
                    room.setNumber(roomNumber);
                    guest.setRoom(room);
                    guest.setCheckInDate(rs.getDate(COLUMN_CHECK_IN_DATE).toLocalDate());
                    guest.setCheckOutDate(rs.getDate(COLUMN_CHECK_OUT_DATE).toLocalDate());
                    loadGuestServices(guest, conn);
                    return guest;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка выборки гостя из БД по id=" + id, e);
            throw new RuntimeException("Ошибка выборки гостя из БД", e);
        }
        return null;
    }

    @Override
    public List<Guest> findAll() {
        List<Guest> list = new ArrayList<>();
        try (Connection conn = jdbcManager.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(SELECT_ALL_GUESTS_SQL)) {
            while (rs.next()) {
                Guest guest = new Guest();
                guest.setId(rs.getInt(COLUMN_ID));
                guest.setName(rs.getString(COLUMN_NAME));
                int roomNumber = rs.getInt(COLUMN_ROOM_NUMBER);
                Room room = new Room();
                room.setNumber(roomNumber);
                guest.setRoom(room);
                guest.setCheckInDate(rs.getDate(COLUMN_CHECK_IN_DATE).toLocalDate());
                guest.setCheckOutDate(rs.getDate(COLUMN_CHECK_OUT_DATE).toLocalDate());
                loadGuestServices(guest, conn);
                list.add(guest);
            }
        } catch (SQLException e) {
            logger.error("Ошибка выборки всех гостей из БД", e);
            throw new RuntimeException("Ошибка выборки всех гостей из БД", e);
        }
        return list;
    }

    @Override
    public Guest update(Guest guest) {
        try (Connection conn = jdbcManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(UPDATE_GUEST_SQL)) {
            statement.setString(1, guest.getName());
            statement.setInt(2, guest.getRoom().getNumber());
            statement.setDate(3, Date.valueOf(guest.getCheckInDate()));
            statement.setDate(4, Date.valueOf(guest.getCheckOutDate()));
            statement.setInt(5, guest.getId());
            statement.executeUpdate();
            try (PreparedStatement delStmt = conn.prepareStatement(DELETE_GUEST_SERVICES_SQL)) {
                delStmt.setInt(1, guest.getId());
                delStmt.executeUpdate();
            }
            for (Service s : guest.getServices()) {
                addServiceToGuest(guest.getId(), s.getId(), conn);
            }
            return guest;
        } catch (SQLException e) {
            logger.error("Ошибка обновления гостя в БД: id=" + guest.getId(), e);
            throw new RuntimeException("Ошибка обновления гостя в БД", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = jdbcManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(DELETE_GUEST_SQL)) {
            statement.setInt(1, id);
            int affected = statement.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Ошибка удаления гостя из БД: id=" + id, e);
            throw new RuntimeException("Ошибка удаления гостя из БД", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = jdbcManager.getConnection();
                Statement statement = conn.createStatement()) {
            statement.executeUpdate(DELETE_GUEST_SERVICES_ALL_SQL);
            statement.executeUpdate(DELETE_GUEST_ALL_SQL);
        } catch (SQLException e) {
            logger.error("Ошибка очистки таблицы guest", e);
            throw new RuntimeException("Ошибка очистки guest", e);
        }
    }

    private void addServiceToGuest(int guestId, int serviceId, Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(INSERT_GUEST_SERVICE_SQL)) {
            statement.setInt(1, guestId);
            statement.setInt(2, serviceId);
            statement.executeUpdate();
        }
    }

    private void loadGuestServices(Guest guest, Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(SELECT_GUEST_SERVICES_SQL)) {
            statement.setInt(1, guest.getId());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt(COLUMN_ID));
                    service.setName(rs.getString(COLUMN_NAME));
                    service.setPrice(rs.getInt(COLUMN_PRICE));
                    guest.addService(service);
                }
            }
        }
    }
}