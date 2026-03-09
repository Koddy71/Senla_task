package ru.ilya.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ilya.io.importer.JsonGuestImporter;
import ru.ilya.io.importer.JsonRoomImporter;
import ru.ilya.io.importer.JsonServiceImporter;
import ru.ilya.io.exporter.JsonGuestExporter;
import ru.ilya.io.exporter.JsonRoomExporter;
import ru.ilya.io.exporter.JsonServiceExporter;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;

import java.util.List;

@Component
public class JsonFileController {

    private static final Logger logger = LoggerFactory.getLogger(JsonFileController.class);

    private final JsonGuestImporter guestImporter;
    private final JsonRoomImporter roomImporter;
    private final JsonServiceImporter serviceImporter;
    private final JsonGuestExporter guestExporter;
    private final JsonRoomExporter roomExporter;
    private final JsonServiceExporter serviceExporter;
    private final RoomService roomService;
    private final GuestService guestService;
    private final ServiceManager serviceManager;

    @Autowired
    public JsonFileController(JsonGuestImporter guestImporter,
            JsonRoomImporter roomImporter,
            JsonServiceImporter serviceImporter,
            JsonGuestExporter guestExporter,
            JsonRoomExporter roomExporter,
            JsonServiceExporter serviceExporter,
            RoomService roomService,
            GuestService guestService,
            ServiceManager serviceManager) {
        this.guestImporter = guestImporter;
        this.roomImporter = roomImporter;
        this.serviceImporter = serviceImporter;
        this.guestExporter = guestExporter;
        this.roomExporter = roomExporter;
        this.serviceExporter = serviceExporter;
        this.roomService = roomService;
        this.guestService = guestService;
        this.serviceManager = serviceManager;
    }

    public void importGuests() {
        logger.info("Начало импорта гостей из JSON");
        try {
            List<Guest> guests = guestImporter.importJson();
            if (guests.isEmpty()) {
                System.out.println("Файл с гостями пуст. Ничего не импортировано.");
                logger.info("Импорт гостей завершён: 0 гостей");
                return;
            }

            for (Guest guest : guests) {
                guestService.checkIn(
                        guest.getName(),
                        guest.getRoom().getNumber(),
                        guest.getCheckInDate(),
                        guest.getCheckOutDate());
            }
            System.out.println("Гости успешно импортированы. Добавлено: " + guests.size());
            logger.info("Импорт гостей завершён: добавлено {} гостей", guests.size());
        } catch (Exception e) {
            logger.error("Ошибка при импорте гостей", e);
            System.out.println("Ошибка при импорте гостей: " + e.getMessage());
        }
    }

    public void importRooms() {
        logger.info("Начало импорта комнат из JSON");
        try {
            List<Room> rooms = roomImporter.importJson();
            if (rooms.isEmpty()) {
                System.out.println("Файл с комнатами пуст. Ничего не импортировано.");
                logger.info("Импорт комнат завершён: 0 комнат");
                return;
            }

            for (Room room : rooms) {
                roomService.addRoom(room);
            }
            System.out.println("Комнаты успешно импортированы. Добавлено: " + rooms.size());
            logger.info("Импорт комнат завершён: добавлено {} комнат", rooms.size());
        } catch (Exception e) {
            logger.error("Ошибка при импорте комнат", e);
            System.out.println("Ошибка при импорте комнат: " + e.getMessage());
        }
    }

    public void importServices() {
        logger.info("Начало импорта услуг из JSON");
        try {
            List<Service> services = serviceImporter.importJson();
            if (services.isEmpty()) {
                System.out.println("Файл с услугами пуст. Ничего не импортировано.");
                logger.info("Импорт услуг завершён: 0 услуг");
                return;
            }

            for (Service service : services) {
                serviceManager.addService(service);
            }
            System.out.println("Услуги успешно импортированы. Добавлено: " + services.size());
            logger.info("Импорт услуг завершён: добавлено {} услуг", services.size());
        } catch (Exception e) {
            logger.error("Ошибка при импорте услуг", e);
            System.out.println("Ошибка при импорте услуг: " + e.getMessage());
        }
    }

    public void exportGuests() {
        logger.info("Начало экспорта гостей в JSON");
        try {
            List<Guest> guests = guestService.getAllGuests();
            guestExporter.exportJson(guests);
            System.out.println("Гости успешно экспортированы.");
            logger.info("Экспорт гостей завершён: сохранено {} гостей", guests.size());
        } catch (Exception e) {
            logger.error("Ошибка при экспорте гостей", e);
            System.out.println("Ошибка при экспорте гостей: " + e.getMessage());
        }
    }

    public void exportRooms() {
        logger.info("Начало экспорта комнат в JSON");
        try {
            List<Room> rooms = roomService.getAllRooms();
            roomExporter.exportJson(rooms);
            System.out.println("Комнаты успешно экспортированы.");
            logger.info("Экспорт комнат завершён: сохранено {} комнат", rooms.size());
        } catch (Exception e) {
            logger.error("Ошибка при экспорте комнат", e);
            System.out.println("Ошибка при экспорте комнат: " + e.getMessage());
        }
    }

    public void exportServices() {
        logger.info("Начало экспорта услуг в JSON");
        try {
            List<Service> services = serviceManager.getAllServices();
            serviceExporter.exportJson(services);
            System.out.println("Услуги успешно экспортированы.");
            logger.info("Экспорт услуг завершён: сохранено {} услуг", services.size());
        } catch (Exception e) {
            logger.error("Ошибка при экспорте услуг", e);
            System.out.println("Ошибка при экспорте услуг: " + e.getMessage());
        }
    }

}