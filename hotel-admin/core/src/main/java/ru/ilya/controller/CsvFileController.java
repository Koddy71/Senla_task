package ru.ilya.controller;

import java.io.IOException;

import ru.ilya.io.importer.GuestImporter;
import ru.ilya.io.importer.RoomImporter;
import ru.ilya.io.importer.ServiceImporter;
import ru.ilya.autodi.Inject;
import ru.ilya.io.exporter.GuestExporter;
import ru.ilya.io.exporter.RoomExporter;
import ru.ilya.io.exporter.ServiceExporter;

public class CsvFileController {
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
        try {
            int imported = guestImporter.importCsv(GUESTS_PATH);

            if (imported == 0) {
                System.out.println("Импорт завершён. Не удалось добавить ни одного гостя.");
            } else {
                System.out.println("Гости успешно импортированы. Добавлено: " + imported);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при импорте гостей: " + e.getMessage());
        }
    }

    public void importRooms() {
        try {
            int imported = roomImporter.importCsv(ROOMS_PATH);

            if (imported == 0) {
                System.out.println("Импорт комнат завершён. Не удалось добавить ни одной комнаты.");
            } else {
                System.out.println("Комнаты успешно импортированы. Добавлено: " + imported);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при импорте комнат: " + e.getMessage());
        }
    }

    public void importServices() {
        try {
            int imported = serviceImporter.importCsv(SERVICES_FILE);

            if (imported == 0) {
                System.out.println("Импорт услуг завершён. Не удалось добавить ни одной услуги.");
            } else {
                System.out.println("Услуги успешно импортированы. Добавлено: " + imported);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при импорте услуг: " + e.getMessage());
        }
    }

    public void exportGuests() {
        try {
            guestExporter.exportCsv(GUESTS_PATH);
            System.out.println("Гости успешно экспортированы.");
        } catch (IOException e) {
            System.out.println("Ошибка при экспорте гостей: " + e.getMessage());
        }
    }

    public void exportRooms() {
        try {
            roomExporter.exportCsv(ROOMS_PATH);
            System.out.println("Комнаты успешно экспортированы.");
        } catch (IOException e) {
            System.out.println("Ошибка при экспорте комнат: " + e.getMessage());
        }
    }

    public void exportServices() {
        try {
            serviceExporter.exportCsv(SERVICES_FILE);
            System.out.println("Услуги успешно экспортированы.");
        } catch (IOException e) {
            System.out.println("Ошибка при экспорте услуг: " + e.getMessage());
        }
    }
}
