package ru.ilya.controller;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;
import ru.ilya.autodi.Inject;
import ru.ilya.autoconfig.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class RoomController {
    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Inject
    private RoomService roomService;

    @Inject
    private AppConfig appConfig;

    private final Scanner sc = new Scanner(System.in);

    public RoomController() {
    }

    private Integer safeInt() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Введите корректное число.");
            logger.error("Integer parsing error: {}", e.getMessage());
            return null;
        }
    }

    public void addRoom() {
        logger.info("Start processing command: addRoom");
        try {
            System.out.print("Введите номер комнаты: ");
            Integer number = safeInt();
            if (number == null) {
                logger.error("addRoom aborted: invalid room number input");
                return;
            }

            System.out.print("Введите цену: ");
            Integer price = safeInt();
            if (price == null) {
                logger.error("addRoom aborted: invalid price input for room {}", number);
                return;
            }

            System.out.print("Введите вместимость: ");
            Integer capacity = safeInt();
            if (capacity == null) {
                logger.error("addRoom aborted: invalid capacity input for room {}", number);
                return;
            }

            System.out.print("Введите количество звёзд: ");
            Integer stars = safeInt();
            if (stars == null) {
                logger.error("addRoom aborted: invalid stars input for room {}", number);
                return;
            }

            Room room = new Room(number, price, capacity, stars);
            boolean ok = roomService.addRoom(room);
            if (ok) {
                System.out.println("Комната добавлена!");
                logger.info("addRoom processed successfully for room {}", number);
            } else {
                System.out.println("Ошибка добавления!");
                logger.error("addRoom failed: service returned false for room {}", number);
            }
        } catch (Exception e) {
            logger.error("Error processing addRoom", e);
            System.out.println("Произошла ошибка при добавлении комнаты: " + e.getMessage());
        }
    }

    public void removeRoom() {
        logger.info("Start processing command: removeRoom");
        try {
            System.out.print("Введите номер комнаты: ");
            Integer number = safeInt();
            if (number == null) {
                logger.error("removeRoom aborted: invalid room number input");
                return;
            }

            boolean ok = roomService.removeRoom(number);
            if (ok) {
                System.out.println("Комната удалена!");
                logger.info("removeRoom processed successfully for room {}", number);
            } else {
                System.out.println("Комната не найдена!");
                logger.error("removeRoom failed: room {} not found", number);
            }
        } catch (Exception e) {
            logger.error("Error processing removeRoom", e);
            System.out.println("Произошла ошибка при удалении комнаты: " + e.getMessage());
        }
    }

    public void showAllRooms() {
        logger.info("Start processing command: showAllRooms");
        try {
            List<Room> rooms = roomService.getAllRooms();
            if (rooms.isEmpty()) {
                System.out.println("Нет комнат.");
                logger.info("showAllRooms processed successfully: no rooms to show");
                return;
            }
            for (Room r : rooms) {
                System.out.println(r.getInfo());
            }
            logger.info("showAllRooms processed successfully: displayed {} rooms", rooms.size());
        } catch (Exception e) {
            logger.error("Error processing showAllRooms", e);
            System.out.println("Произошла ошибка при показе всех комнат: " + e.getMessage());
        }
    }

    public void findRoomByNumber() {
        logger.info("Start processing command: findRoomByNumber");
        try {
            System.out.print("Введите номер комнаты: ");
            Integer number = safeInt();
            if (number == null) {
                logger.error("findRoomByNumber aborted: invalid room number input");
                return;
            }

            Room r = roomService.findRoom(number);
            if (r != null) {
                System.out.println(r.getInfo());
                logger.info("findRoomByNumber processed successfully: found room {}", number);
            } else {
                System.out.println("Комната не найдена.");
                logger.error("findRoomByNumber failed: room {} not found", number);
            }
        } catch (Exception e) {
            logger.error("Error processing findRoomByNumber", e);
            System.out.println("Произошла ошибка при поиске комнаты: " + e.getMessage());
        }
    }

    public void changeRoomStatus() {
        logger.info("Start processing command: changeRoomStatus");
        try {
            if (!appConfig.isRoomStatusChangeEnable()) {
                System.out.println("Изменение статуса отключено (config.properties)");
                logger.info("changeRoomStatus aborted: disabled in config");
                return;
            }

            System.out.print("Введите номер комнаты: ");
            Integer number = safeInt();
            if (number == null) {
                logger.error("changeRoomStatus aborted: invalid room number input");
                return;
            }

            System.out.print("Введите статус (AVAILABLE / OCCUPIED / MAINTENANCE / RESERVED): ");
            String s = sc.nextLine().trim().toUpperCase();

            try {
                RoomStatus status = RoomStatus.valueOf(s);
                boolean ok = roomService.changeStatus(number, status);
                if (ok) {
                    System.out.println("Статус изменён!");
                    logger.info("changeRoomStatus processed successfully for room {} -> {}", number, status);
                } else {
                    System.out.println("Комната не найдена.");
                    logger.error("changeRoomStatus failed: room {} not found", number);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный статус.");
                logger.error("changeRoomStatus aborted: invalid status '{}'", s);
            }
        } catch (Exception e) {
            logger.error("Error processing changeRoomStatus", e);
            System.out.println("Произошла ошибка при изменении статуса комнаты: " + e.getMessage());
        }
    }

    public void getRoomsFreeByDate() {
        logger.info("Start processing command: getRoomsFreeByDate");
        try {
            System.out.print("Введите дату (гггг-мм-дд): ");
            String input = sc.nextLine();

            LocalDate date;
            try {
                date = LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты.");
                logger.error("getRoomsFreeByDate aborted: invalid date format '{}'", input);
                return;
            }

            List<Room> rooms = roomService.getRoomsFreeByDate(date);
            if (rooms.isEmpty()) {
                System.out.println("Нет доступных комнат на эту дату.");
                logger.info("getRoomsFreeByDate processed successfully: no free rooms on {}", date);
            } else {
                System.out.println("Доступные комнаты:");
                for (Room r : rooms) {
                    System.out.println(r.getInfo());
                }
                logger.info("getRoomsFreeByDate processed successfully: {} rooms free on {}", rooms.size(), date);
            }
        } catch (Exception e) {
            logger.error("Error processing getRoomsFreeByDate", e);
            System.out.println("Произошла ошибка при поиске доступных комнат: " + e.getMessage());
        }
    }

    public void sortRooms() {
        logger.info("Start processing command: sortRooms");
        try {
            System.out.print("Сортировать по ('price', 'capacity', 'stars'): ");
            String sortBy = sc.nextLine();

            List<Room> sorted = roomService.getRoomsSorted(sortBy);
            for (Room r : sorted) {
                System.out.println(r.getInfo());
            }
            logger.info("sortRooms processed successfully by '{}', returned {} rooms", sortBy, sorted.size());
        } catch (Exception e) {
            logger.error("Error processing sortRooms", e);
            System.out.println("Произошла ошибка при сортировке комнат: " + e.getMessage());
        }
    }
}
