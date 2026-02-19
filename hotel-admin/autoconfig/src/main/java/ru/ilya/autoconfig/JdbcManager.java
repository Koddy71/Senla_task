package ru.ilya.autoconfig;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JdbcManager {
    private static final Logger logger = LoggerFactory.getLogger(JdbcManager.class);
    private static final String CONFIG_PATH = "core/src/main/resources/config.properties";
    private final Properties props = new Properties();

    private final ThreadLocal<Connection> transactionalConnection = new ThreadLocal<>();
    private final ThreadLocal<Integer> transactionDepth = ThreadLocal.withInitial(() -> 0);

    public JdbcManager() {
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            props.load(reader);
            Class.forName(props.getProperty("db.driver"));
            logger.info("JdbcManager инициализирован, драйвер загружен: {}", props.getProperty("db.driver"));
        } catch (IOException e) {
            logger.error("Не удалось загрузить файл конфигурации: {}", CONFIG_PATH, e);
            throw new RuntimeException("Не удалось загрузить файл конфигурации: " + CONFIG_PATH, e);
        } catch (ClassNotFoundException e) {
            logger.error("Класс JDBC драйвера не найден: {}", props.getProperty("db.driver"), e);
            throw new RuntimeException("JDBC Driver не найден", e);
        }
    }

    public Connection getConnection() throws SQLException {
        Connection txConnection = transactionalConnection.get();
        if (txConnection != null) {
            return (Connection) Proxy.newProxyInstance( // Прокси, который не закрывает соединение
                    Connection.class.getClassLoader(),
                    new Class[] { Connection.class },
                    (proxy, method, args) -> {
                        if ("close".equals(method.getName())) {
                            return null;
                        }
                        return method.invoke(txConnection, args);
                    });
        }

        try{
            return DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password"));
        } catch (SQLException e){
            logger.error("Ошибка получения соединения с базой данных", e);
            throw e;
        }
    }

    public void beginTransaction() throws SQLException {
        if (transactionDepth.get() == 0) {
            logger.info("Начало новой транзакции");
            Connection connection = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password"));
            connection.setAutoCommit(false);
            transactionalConnection.set(connection);
        }
        transactionDepth.set(transactionDepth.get() + 1);
    }

    public void commitTransaction() throws SQLException {
        int depth = transactionDepth.get() - 1;
        transactionDepth.set(depth);

        if (depth == 0) {
            Connection connection = transactionalConnection.get();
            if (connection != null) {
                try {
                    connection.commit();
                    logger.info("Транзакция успешно зафиксирована");
                } finally {
                    connection.close();
                    transactionalConnection.remove();
                }
            }
        }
    }

    public void rollbackTransaction() {
        try {
            Connection connection = transactionalConnection.get();
            if (connection != null) {
                connection.rollback();
                logger.info("Выполнен rollback транзакции");
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Ошибка при выполнении rollback транзакции", e);
            throw new RuntimeException("Ошибка rollback транзакции", e);
        } finally {
            transactionalConnection.remove();
            transactionDepth.remove();
        }
    }
}
