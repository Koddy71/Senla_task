package ru.ilya.controller;

import java.io.IOException;

import ru.ilya.io.importer.GuestImporter;
import ru.ilya.io.importer.RoomImporter;
import ru.ilya.io.importer.ServiceImporter;
import ru.ilya.autodi.Inject;
import ru.ilya.io.exporter.GuestExporter;
import ru.ilya.io.exporter.RoomExporter;
import ru.ilya.io.exporter.ServiceExporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvFileController {

    private static final Logger logger = LoggerFactory.getLogger(CsvFileController.class);

    private static final String ROOMS_PATH = "core/src/main/resources/rooms.csv";
    private static final String GUESTS_PATH = "core/src/main/resources/guests.csv";
    private static final String SERVICES_FILE = "core/src/main/resources/services.csv";

    @Inject
    private GuestImporter guestImporter;

    @Inject
    private RoomImporter roomImporter;

    @Inject
    private ServiceImporter serviceImporter;

    @Inject
    private GuestExporter guestExporter;

    @Inject
    private RoomExporter roomExporter;

    @Inject
    private ServiceExporter serviceExporter;

    public CsvFileController() {
    }

    public void importGuests() {
        logger.info("Начало обработки команды: importGuests");
        try {
            int imported = guestImporter.importCsv(GUESTS_PATH);

            if (imported == 0) {
                System.out.println("Импорт завершён. Не удалось добавить ни одного гостя.");
                logger.info("importGuests завершён: импортировано 0 гостей");
            } else {
                System.out.println("Гости успешно импортированы. Добавлено: " + imported);
                logger.info("importGuests успешно выполнен: импортировано {} гостей", imported);
            }

        } catch (IOException e) {
            logger.error("Ошибка при выполнении importGuests", e);
            System.out.println("Ошибка при импорте гостей: " + e.getMessage());
        }
    }

    public void importRooms() {
        logger.info("Начало обработки команды: importRooms");
        try {
            int imported = roomImporter.importCsv(ROOMS_PATH);

            if (imported == 0) {
                System.out.println("Импорт комнат завершён. Не удалось добавить ни одной комнаты.");
                logger.info("importRooms завершён: импортировано 0 комнат");
            } else {
                System.out.println("Комнаты успешно импортированы. Добавлено: " + imported);
                logger.info("importRooms успешно выполнен: импортировано {} комнат", imported);
            }

        } catch (IOException e) {
            logger.error("Ошибка при выполнении importRooms", e);
            System.out.println("Ошибка при импорте комнат: " + e.getMessage());
        }
    }

    public void importServices() {
        logger.info("Начало обработки команды: importServices");
        try {
            int imported = serviceImporter.importCsv(SERVICES_FILE);

            if (imported == 0) {
                System.out.println("Импорт услуг завершён. Не удалось добавить ни одной услуги.");
                logger.info("importServices завершён: импортировано 0 услуг");
            } else {
                System.out.println("Услуги успешно импортированы. Добавлено: " + imported);
                logger.info("importServices успешно выполнен: импортировано {} услуг", imported);
            }

        } catch (IOException e) {
            logger.error("Ошибка при выполнении importServices", e);
            System.out.println("Ошибка при импорте услуг: " + e.getMessage());
        }
    }

    public void exportGuests() {
        logger.info("Начало обработки команды: exportGuests");
        try {
            guestExporter.exportCsv(GUESTS_PATH);
            System.out.println("Гости успешно экспортированы.");
            logger.info("exportGuests успешно выполнен");

        } catch (IOException e) {
            logger.error("Ошибка при выполнении exportGuests", e);
            System.out.println("Ошибка при экспорте гостей: " + e.getMessage());
        }
    }

    public void exportRooms() {
        logger.info("Начало обработки команды: exportRooms");
        try {
            roomExporter.exportCsv(ROOMS_PATH);
            System.out.println("Комнаты успешно экспортированы.");
            logger.info("exportRooms успешно выполнен");

        } catch (IOException e) {
            logger.error("Ошибка при выполнении exportRooms", e);
            System.out.println("Ошибка при экспорте комнат: " + e.getMessage());
        }
    }

    public void exportServices() {
        logger.info("Начало обработки команды: exportServices");
        try {
            serviceExporter.exportCsv(SERVICES_FILE);
            System.out.println("Услуги успешно экспортированы.");
            logger.info("exportServices успешно выполнен");

        } catch (IOException e) {
            logger.error("Ошибка при выполнении exportServices", e);
            System.out.println("Ошибка при экспорте услуг: " + e.getMessage());
        }
    }
}