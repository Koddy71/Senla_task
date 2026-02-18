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
        logger.info("Start processing command: importGuests");
        try {
            int imported = guestImporter.importCsv(GUESTS_PATH);

            if (imported == 0) {
                System.out.println("Импорт завершён. Не удалось добавить ни одного гостя.");
                logger.info("importGuests finished: 0 guests imported");
            } else {
                System.out.println("Гости успешно импортированы. Добавлено: " + imported);
                logger.info("importGuests processed successfully: {} guests imported", imported);
            }

        } catch (IOException e) {
            logger.error("Error processing importGuests", e);
            System.out.println("Ошибка при импорте гостей: " + e.getMessage());
        }
    }

    public void importRooms() {
        logger.info("Start processing command: importRooms");
        try {
            int imported = roomImporter.importCsv(ROOMS_PATH);

            if (imported == 0) {
                System.out.println("Импорт комнат завершён. Не удалось добавить ни одной комнаты.");
                logger.info("importRooms finished: 0 rooms imported");
            } else {
                System.out.println("Комнаты успешно импортированы. Добавлено: " + imported);
                logger.info("importRooms processed successfully: {} rooms imported", imported);
            }

        } catch (IOException e) {
            logger.error("Error processing importRooms", e);
            System.out.println("Ошибка при импорте комнат: " + e.getMessage());
        }
    }

    public void importServices() {
        logger.info("Start processing command: importServices");
        try {
            int imported = serviceImporter.importCsv(SERVICES_FILE);

            if (imported == 0) {
                System.out.println("Импорт услуг завершён. Не удалось добавить ни одной услуги.");
                logger.info("importServices finished: 0 services imported");
            } else {
                System.out.println("Услуги успешно импортированы. Добавлено: " + imported);
                logger.info("importServices processed successfully: {} services imported", imported);
            }

        } catch (IOException e) {
            logger.error("Error processing importServices", e);
            System.out.println("Ошибка при импорте услуг: " + e.getMessage());
        }
    }

    public void exportGuests() {
        logger.info("Start processing command: exportGuests");
        try {
            guestExporter.exportCsv(GUESTS_PATH);
            System.out.println("Гости успешно экспортированы.");
            logger.info("exportGuests processed successfully");

        } catch (IOException e) {
            logger.error("Error processing exportGuests", e);
            System.out.println("Ошибка при экспорте гостей: " + e.getMessage());
        }
    }

    public void exportRooms() {
        logger.info("Start processing command: exportRooms");
        try {
            roomExporter.exportCsv(ROOMS_PATH);
            System.out.println("Комнаты успешно экспортированы.");
            logger.info("exportRooms processed successfully");

        } catch (IOException e) {
            logger.error("Error processing exportRooms", e);
            System.out.println("Ошибка при экспорте комнат: " + e.getMessage());
        }
    }

    public void exportServices() {
        logger.info("Start processing command: exportServices");
        try {
            serviceExporter.exportCsv(SERVICES_FILE);
            System.out.println("Услуги успешно экспортированы.");
            logger.info("exportServices processed successfully");

        } catch (IOException e) {
            logger.error("Error processing exportServices", e);
            System.out.println("Ошибка при экспорте услуг: " + e.getMessage());
        }
    }
}
