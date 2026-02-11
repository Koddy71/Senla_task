package ru.ilya.service.impl;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.autoconfig.DatabaseManager;
import ru.ilya.controller.CsvFileController;
import ru.ilya.controller.JsonFileController;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.service.StateRestoreService;
import ru.ilya.controller.DaoController;
import ru.ilya.autodi.Inject;

import java.util.List;

public class StateRestoreServiceImpl implements StateRestoreService {

    @Inject
    private AppConfig config;

    @Inject
    private RoomService roomService;

    @Inject
    private GuestService guestService;

    @Inject
    private ServiceManager serviceManager;

    @Inject
    private CsvFileController csvFileController;

    @Inject
    private DaoController daoController;

    @Inject
    private JsonFileController jsonFileController;

    @Inject
    private DatabaseManager dbManager;

    @Override
    public void restore() {
        if ("json".equalsIgnoreCase(config.getStorageType())) {
            restoreFromJson();
        } else if ("csv".equalsIgnoreCase(config.getStorageType())) {
            restoreFromCsv();
        } else if ("db".equalsIgnoreCase(config.getStorageType())) {
            restoreFromDb();
        } else {
            throw new RuntimeException(
                    "Неизвестный тип хранилища: " + config.getStorageType());
        }
    }

    @Override
    public void save() {
        if ("json".equalsIgnoreCase(config.getStorageType())) {
            saveToJson();
        } else if ("csv".equalsIgnoreCase(config.getStorageType())) {
            saveToCsv();
        } else if ("db".equalsIgnoreCase(config.getStorageType())) {
            saveToDb();
        } else {
            throw new RuntimeException(
                    "Неизвестный тип хранилища: " + config.getStorageType());
        }
    }

    private void restoreFromJson() {
        List<Room> rooms = jsonFileController.loadRooms();
        List<Guest> guests = jsonFileController.loadGuests();
        List<Service> services = jsonFileController.loadServices();

        if (rooms.isEmpty() && guests.isEmpty() && services.isEmpty()) {
            System.out.println("JSON пуст. Нечего восстанавливать.");
            return;
        }

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
    }

    private void restoreFromCsv() {
        System.out.println("Импорт данных из CSV...");
        csvFileController.importRooms();
        csvFileController.importServices();
        csvFileController.importGuests();
    }

    private void restoreFromDb() {
        System.out.println("Загрузка данных из БД...");
        daoController.restoreRooms(roomService);
        daoController.restoreServices(serviceManager);
        daoController.restoreGuests(roomService, guestService);
        System.out.println("Данные из БД загружены.");
    }

    private void saveToJson() {
        jsonFileController.saveGuests(guestService.getAllGuests());
        System.out.println("Гости сохранены.");

        jsonFileController.saveRooms(roomService.getAllRooms());
        System.out.println("Комнаты сохранены.");

        jsonFileController.saveServices(serviceManager.getAllServices());
        System.out.println("Услуги сохранены.");
    }

    private void saveToCsv() {
        csvFileController.exportRooms();
        csvFileController.exportServices();
        csvFileController.exportGuests();

        System.out.println("Состояние сохранено в CSV.");
    }

    private void saveToDb() {
        System.out.println("Сохранение данных в БД...");
        try {
            dbManager.beginTransaction();

            daoController.clearDatabase();
            daoController.saveRooms(roomService);
            daoController.saveServices(serviceManager);
            daoController.saveGuests(guestService);

            dbManager.commitTransaction();
        } catch (Exception e) {
            dbManager.rollbackTransaction();
            throw new RuntimeException("Ошибка сохранения состояния в БД. Транзакция откатилась.", e);
        }
        System.out.println("Данные сохранены в БД.");
    }
}
