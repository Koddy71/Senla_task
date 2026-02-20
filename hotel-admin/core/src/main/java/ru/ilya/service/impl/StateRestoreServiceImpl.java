package ru.ilya.service.impl;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.autoconfig.JdbcManager;
import ru.ilya.controller.CsvFileController;
import ru.ilya.controller.JsonFileController;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.service.StateRestoreService;
import ru.ilya.controller.JdbcController;
import ru.ilya.controller.JpaController;
import ru.ilya.exceptions.ApplicationException;
import ru.ilya.exceptions.PersistenceException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StateRestoreServiceImpl implements StateRestoreService {

    private static final Logger logger = LoggerFactory.getLogger(StateRestoreServiceImpl.class);

    @Autowired
    private AppConfig config;

    @Autowired
    private RoomService roomService;

    @Autowired
    private GuestService guestService;

    @Autowired
    private ServiceManager serviceManager;

    @Autowired
    private CsvFileController csvFileController;

    @Autowired
    private JdbcController jdbcController;

    @Autowired
    private JsonFileController jsonFileController;

    @Autowired
    private JdbcManager jdbcManager;

    @Autowired
    private JpaController jpaController;

    public StateRestoreServiceImpl() {
    }

    @Override
    public void restore() {
        String storageType = config.getStorageType();
        logger.info("Начало восстановления состояния из хранилища: {}", storageType);

        if ("json".equalsIgnoreCase(storageType)) {
            restoreFromJson();
        } else if ("csv".equalsIgnoreCase(storageType)) {
            restoreFromCsv();
        } else if ("jdbc".equalsIgnoreCase(storageType)) {
            restoreFromJdbc();
        } else if ("jpa".equalsIgnoreCase(storageType)) {
            restoreFromJpa();
        } else {
            logger.error("Неизвестный тип хранилища: {}", storageType);
            throw new ApplicationException("Неизвестный тип хранилища: " + storageType);
        }

        logger.info("Восстановление состояния завершено");
    }

    @Override
    public void save() {
        String storageType = config.getStorageType();
        logger.info("Начало сохранения состояния в хранилище: {}", storageType);

        if ("json".equalsIgnoreCase(storageType)) {
            saveToJson();
        } else if ("csv".equalsIgnoreCase(storageType)) {
            saveToCsv();
        } else if ("jdbc".equalsIgnoreCase(storageType)) {
            saveToJdbc();
        } else if ("jpa".equalsIgnoreCase(storageType)) {
            saveToJpa();
        } else {
            logger.error("Неизвестный тип хранилища: {}", storageType);
            throw new ApplicationException("Неизвестный тип хранилища: " + storageType);
        }

        logger.info("Сохранение состояния завершено");
    }

    private void restoreFromJson() {
        logger.info("Восстановление из JSON");
        List<Room> rooms = jsonFileController.loadRooms();
        List<Guest> guests = jsonFileController.loadGuests();
        List<Service> services = jsonFileController.loadServices();

        if (rooms.isEmpty() && guests.isEmpty() && services.isEmpty()) {
            System.out.println("JSON пуст. Нечего восстанавливать.");
            logger.info("JSON пуст, восстановление не требуется");
            return;
        }

        logger.info("Загружено из JSON: комнат {}, гостей {}, услуг {}", rooms.size(), guests.size(), services.size());

        for (Room room : rooms) {
            roomService.addRoom(room);
        }

        for (Guest guest : guests) {
            guestService.checkIn(
                    guest.getName(),
                    guest.getRoom().getNumber(),
                    guest.getCheckInDate(),
                    guest.getCheckOutDate());
        }

        for (Service s : services) {
            serviceManager.addService(s);
        }

        System.out.println("Состояние восстановлено из JSON.");
        logger.info("Восстановление из JSON завершено");
    }

    private void restoreFromCsv() {
        logger.info("Восстановление из CSV");
        System.out.println("Импорт данных из CSV...");
        csvFileController.importRooms();
        csvFileController.importServices();
        csvFileController.importGuests();
        logger.info("Восстановление из CSV завершено");
    }

    private void restoreFromJdbc() {
        logger.info("Восстановление из JDBC");
        System.out.println("Загрузка данных из БД...");
        jdbcController.restoreRooms(roomService);
        jdbcController.restoreServices(serviceManager);
        jdbcController.restoreGuests(roomService, guestService);
        System.out.println("Данные из БД загружены.");
        logger.info("Восстановление из JDBC завершено");
    }

    private void restoreFromJpa() {
        logger.info("Восстановление из JPA");
        System.out.println("Загрузка данных из БД...");
        jpaController.restoreRooms(roomService);
        jpaController.restoreServices(serviceManager);
        jpaController.restoreGuests(roomService, guestService);
        System.out.println("Данные из БД загружены.");
        logger.info("Восстановление из JPA завершено");
    }

    private void saveToJson() {
        logger.info("Сохранение в JSON");
        jsonFileController.saveGuests(guestService.getAllGuests());
        System.out.println("Гости сохранены.");

        jsonFileController.saveRooms(roomService.getAllRooms());
        System.out.println("Комнаты сохранены.");

        jsonFileController.saveServices(serviceManager.getAllServices());
        System.out.println("Услуги сохранены.");

        logger.info("Сохранение в JSON завершено");
    }

    private void saveToCsv() {
        logger.info("Сохранение в CSV");
        csvFileController.exportRooms();
        csvFileController.exportServices();
        csvFileController.exportGuests();

        System.out.println("Состояние сохранено в CSV.");
        logger.info("Сохранение в CSV завершено");
    }

    private void saveToJdbc() {
        logger.info("Сохранение в JDBC");
        System.out.println("Сохранение данных в БД...");
        try {
            jdbcManager.beginTransaction();
            logger.info("Транзакция JDBC начата");

            jdbcController.clearDatabase();
            jdbcController.saveRooms(roomService);
            jdbcController.saveServices(serviceManager);
            jdbcController.saveGuests(guestService);

            jdbcManager.commitTransaction();
            logger.info("Транзакция JDBC зафиксирована");
        } catch (Exception e) {
            jdbcManager.rollbackTransaction();
            logger.error("Ошибка сохранения состояния в БД, транзакция откачена", e);
            throw new PersistenceException("Ошибка сохранения состояния в БД. Транзакция откатилась.", e);
        }
        System.out.println("Данные сохранены в БД.");
        logger.info("Сохранение в JDBC завершено");
    }

    private void saveToJpa() {
        logger.info("Сохранение в JPA");
        System.out.println("Сохранение данных в БД...");
        try {
            jpaController.clearDatabase();
            jpaController.saveRooms(roomService);
            jpaController.saveServices(serviceManager);
            jpaController.saveGuests(guestService);

        } catch (Exception e) {
            logger.error("Ошибка сохранения состояния в БД (JPA)", e);
            throw new PersistenceException("Ошибка сохранения состояния в БД.", e);
        }
        System.out.println("Данные сохранены в БД.");
        logger.info("Сохранение в JPA завершено");
    }
}