package ru.ilya.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.autoconfig.JdbcManager;
import ru.ilya.autodi.Inject;
import ru.ilya.dao.GenericDao;
import ru.ilya.model.Service;

public class ServiceDaoJdbc implements GenericDao<Service, Integer> {
    private static final String INSERT_SQL = "INSERT INTO service(id, name, price) VALUES (?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT id, name, price FROM service WHERE id = ?";
    private static final String SELECT_ALL_SQL = "SELECT id, name, price FROM service";
    private static final String UPDATE_SQL = "UPDATE service SET name = ?, price = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM service WHERE id = ?";
    private static final String DELETE_ALL_SQL = "DELETE FROM service";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";

    @Inject
    private JdbcManager jdbcManager;

    public ServiceDaoJdbc() {
    }

    @Override
    public Service create(Service service) {
        try (Connection conn = jdbcManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(INSERT_SQL)) {
            statement.setInt(1, service.getId());
            statement.setString(2, service.getName());
            statement.setInt(3, service.getPrice());
            statement.executeUpdate();
            return service;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка вставки услуги в БД", e);
        }
    }

    @Override
    public Service findById(Integer id) {
        try (Connection conn = jdbcManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt(COLUMN_ID));
                    service.setName(rs.getString(COLUMN_NAME));
                    service.setPrice(rs.getInt(COLUMN_PRICE));
                    return service;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выборки услуги из БД", e);
        }
        return null;
    }

    @Override
    public List<Service> findAll() {
        List<Service> list = new ArrayList<>();
        try (Connection conn = jdbcManager.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt(COLUMN_ID));
                service.setName(rs.getString(COLUMN_NAME));
                service.setPrice(rs.getInt(COLUMN_PRICE));
                list.add(service);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выборки всех услуг из БД", e);
        }
        return list;
    }

    @Override
    public Service update(Service service) {
        try (Connection conn = jdbcManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, service.getName());
            statement.setInt(2, service.getPrice());
            statement.setInt(3, service.getId());
            statement.executeUpdate();
            return service;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления услуги в БД", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = jdbcManager.getConnection();
                PreparedStatement statement = conn.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, id);
            int affected = statement.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления услуги из БД", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = jdbcManager.getConnection();
                Statement statement = conn.createStatement()) {
            statement.executeUpdate(DELETE_ALL_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка очистки таблицы service", e);
        }
    }
}
